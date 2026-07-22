package com.newbranch.backend.domain.attachment;

import com.fasterxml.jackson.databind.JsonNode;
import com.newbranch.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 51~65번 (Attachment 도메인) 검증
 */
class AttachmentControllerTest extends IntegrationTestSupport {

    // 51. 파일 업로드 정상
    @Test
    @DisplayName("유효한 PDF 파일을 업로드하면 201과 함께 S3 업로드 결과가 반환된다")
    void uploadFiles_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        MockMultipartFile file = new MockMultipartFile("files", "note.pdf", "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/v1/attachments/file").file(file).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data[0].fileName").value("note.pdf"))
                .andExpect(jsonPath("$.data[0].attachmentType").value("FILE"));
    }

    // 52. 지원하지 않는 확장자
    @Test
    @DisplayName("지원하지 않는 파일 형식을 업로드하면 400 INVALID_FILE_TYPE을 반환한다")
    void uploadFiles_unsupportedType() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        MockMultipartFile file = new MockMultipartFile("files", "malware.exe", "application/x-msdownload", "exe-content".getBytes());

        mockMvc.perform(multipart("/api/v1/attachments/file").file(file).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AT002"));
    }

    // 53. 파일 크기 초과 (테스트 프로파일에서 10KB로 제한)
    // MockMvc의 MOCK 웹 환경은 실제 서블릿 컨테이너 단의 업로드 용량 제한을 적용하지 않으므로,
    // 이 케이스만 실제 내장 서버(RANDOM_PORT)에 TestRestTemplate으로 요청해 검증한다.
    @Test
    @DisplayName("허용된 용량을 초과하는 파일을 업로드하면 400을 반환한다")
    void uploadFiles_tooLarge() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        byte[] oversized = new byte[20 * 1024];
        ByteArrayResource resource = new ByteArrayResource(oversized) {
            @Override
            public String getFilename() {
                return "big.pdf";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("files", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", bearer(user.accessToken()));

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/attachments/file",
                new HttpEntity<>(body, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("\"code\":\"C002\"");
    }

    // 54. 강의 녹음 업로드 정상
    @Test
    @DisplayName("유효한 오디오 파일을 업로드하면 201과 함께 Whisper STT 처리 결과가 반환된다")
    void uploadLecture_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        Mockito.when(openAiClient.transcribeAudio(Mockito.any())).thenReturn("강의 전사 텍스트");
        MockMultipartFile file = new MockMultipartFile("file", "lecture.mp3", "audio/mpeg", "audio-content".getBytes());

        mockMvc.perform(multipart("/api/v1/attachments/lecture").file(file).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attachmentType").value("LECTURE"))
                .andExpect(jsonPath("$.data.content").doesNotExist());
    }

    // 55. 잘못된 파일 형식 (오디오가 아닌 파일)
    @Test
    @DisplayName("오디오가 아닌 파일을 강의 업로드하면 400 INVALID_FILE_TYPE을 반환한다")
    void uploadLecture_invalidType() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        MockMultipartFile file = new MockMultipartFile("file", "note.pdf", "application/pdf", "pdf-content".getBytes());

        mockMvc.perform(multipart("/api/v1/attachments/lecture").file(file).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("AT002"));
    }

    // 56. 텍스트 저장 정상
    @Test
    @DisplayName("유효한 텍스트를 저장하면 201을 반환한다")
    void saveText_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"title":"필기노트","content":"수업 중 필기한 내용입니다."}
                """;

        mockMvc.perform(post("/api/v1/attachments/text")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attachmentType").value("TEXT"));
    }

    // 57. 빈 텍스트 저장 요청
    @Test
    @DisplayName("빈 값으로 텍스트 저장을 요청하면 400 validation 오류를 반환한다")
    void saveText_blank() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"title":"필기노트","content":"   "}
                """;

        mockMvc.perform(post("/api/v1/attachments/text")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 58. URL 저장 정상(VIDEO)
    @Test
    @DisplayName("유효한 영상 URL을 저장하면 201을 반환한다")
    void saveUrl_video_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"type":"VIDEO","url":"https://youtube.com/watch?v=abc123","title":"강의 영상"}
                """;

        mockMvc.perform(post("/api/v1/attachments/url")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attachmentType").value("VIDEO"));
    }

    // 59. URL 저장 정상(WEB)
    @Test
    @DisplayName("유효한 웹페이지 URL을 저장하면 201을 반환한다")
    void saveUrl_web_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"type":"WEB","url":"https://example.com/article","title":"참고 자료"}
                """;

        mockMvc.perform(post("/api/v1/attachments/url")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.attachmentType").value("WEB"));
    }

    // 60. 잘못된 URL 형식 (AttachmentUrlRequest.url에 @Pattern 추가로 검증됨)
    @Test
    @DisplayName("형식이 잘못된 URL을 저장 요청하면 400 validation 오류를 반환한다")
    void saveUrl_invalidFormat() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"type":"WEB","url":"not-a-url","title":"참고 자료"}
                """;

        mockMvc.perform(post("/api/v1/attachments/url")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 61. 첨부파일 목록 조회
    @Test
    @DisplayName("첨부파일이 있으면 목록 조회 시 200과 목록을 반환한다")
    void getAttachments_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        saveText(user, "노트1");

        mockMvc.perform(get("/api/v1/attachments").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // 62. 첨부파일 상세 조회 정상
    @Test
    @DisplayName("본인 첨부파일을 상세 조회하면 200과 상세 정보를 반환한다")
    void getAttachment_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long attachmentId = saveText(user, "상세조회노트");

        mockMvc.perform(get("/api/v1/attachments/" + attachmentId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("상세조회노트"));
    }

    // 63. 존재하지 않는 첨부파일 조회
    @Test
    @DisplayName("존재하지 않는 attachmentId를 조회하면 404 ATTACHMENT_NOT_FOUND를 반환한다")
    void getAttachment_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/v1/attachments/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AT001"));
    }

    // 64. 첨부파일 삭제 정상
    @Test
    @DisplayName("본인 첨부파일을 삭제하면 200을 반환하고 이후 조회 시 404가 된다")
    void deleteAttachment_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long attachmentId = saveText(user, "삭제될노트");

        mockMvc.perform(delete("/api/v1/attachments/" + attachmentId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/attachments/" + attachmentId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AT001"));
    }

    // 65. 존재하지 않는 첨부파일 삭제
    @Test
    @DisplayName("존재하지 않는 attachmentId를 삭제하면 404 ATTACHMENT_NOT_FOUND를 반환한다")
    void deleteAttachment_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(delete("/api/v1/attachments/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AT001"));
    }

    private long saveText(IntegrationTestSupport.TestUser user, String title) throws Exception {
        String body = """
                {"title":"%s","content":"내용"}
                """.formatted(title);
        MvcResult result = mockMvc.perform(post("/api/v1/attachments/text")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("data").path("attachmentId").asLong();
    }
}
