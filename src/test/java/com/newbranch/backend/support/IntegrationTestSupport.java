package com.newbranch.backend.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.infra.extraction.TextExtractionService;
import com.newbranch.backend.infra.openai.OpenAiClient;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// RANDOM_PORT (rather than MOCK) so multipart size-limit enforcement, which only happens at the
// real servlet-container level, can actually be exercised via TestRestTemplate where needed.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    // loginId has a max length of 15 (`user` prefix + suffix), so keep the seed short.
    private static final AtomicLong SEQ = new AtomicLong(System.nanoTime() % 1_000_000);

    @LocalServerPort
    protected int port;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    // External services are never actually called in tests - mocked out.
    @MockBean
    protected JavaMailSender javaMailSender;

    @MockBean
    protected OpenAiClient openAiClient;

    @MockBean
    protected TextExtractionService textExtractionService;

    // Mocking the low-level S3Client (rather than S3Uploader itself) keeps S3Uploader's real
    // content-type validation logic under test while still avoiding any real AWS network call.
    @MockBean
    protected software.amazon.awssdk.services.s3.S3Client s3Client;

    @BeforeEach
    void setUpDefaultMocks() {
        // Mockito returns null from createMimeMessage() unless stubbed, which NPEs inside MimeMessageHelper.
        org.mockito.Mockito.when(javaMailSender.createMimeMessage())
                .thenReturn(new MimeMessage(Session.getDefaultInstance(new java.util.Properties())));
    }

    protected String uniqueSuffix() {
        return String.valueOf(SEQ.incrementAndGet());
    }

    /**
     * Signs a fresh user up and logs in through the real HTTP endpoints, returning the issued accessToken.
     */
    protected TestUser signupAndLogin() throws Exception {
        String suffix = uniqueSuffix();
        String loginId = "user" + suffix;
        String email = "user" + suffix + "@test.com";
        String password = "password123!";
        String nickname = "nick" + suffix;

        String signupBody = """
                {
                  "loginId": "%s",
                  "email": "%s",
                  "password": "%s",
                  "nickname": "%s"
                }
                """.formatted(loginId, email, password, nickname);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType("application/json")
                        .content(signupBody))
                .andExpect(status().isCreated());

        String loginBody = """
                {
                  "loginId": "%s",
                  "password": "%s"
                }
                """.formatted(loginId, password);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String accessToken = json.path("data").path("accessToken").asText();
        String refreshToken = json.path("data").path("refreshToken").asText();

        return new TestUser(loginId, email, password, nickname, accessToken, refreshToken);
    }

    protected String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }

    public record TestUser(String loginId, String email, String password, String nickname,
                            String accessToken, String refreshToken) {
    }
}
