package com.newbranch.backend.infra.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

    private final RestClient openAiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${openai.whisper-model:whisper-large-v3}")
    private String whisperModel;

    /**
     * OpenAI Chat Completions API를 호출하고 JSON 응답 문자열을 반환합니다.
     *
     * @param systemPrompt 시스템 프롬프트
     * @param userPrompt   사용자 프롬프트
     * @return GPT가 생성한 JSON 문자열
     */
    public String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = buildRequestBody(systemPrompt, userPrompt);
        String rawResponse = callApi(body);
        return extractContent(rawResponse);
    }

    private Map<String, Object> buildRequestBody(String systemPrompt, String userPrompt) {
        return Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "response_format", Map.of("type", "json_object"),
                "temperature", 0.3,
                "max_tokens", 4096
        );
    }

    private String callApi(Map<String, Object> body) {
        try {
            return openAiRestClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    /**
     * OpenAI Whisper API를 호출하여 오디오 파일을 텍스트로 변환합니다.
     * 파일 크기 제한: 25MB (OpenAI 정책)
     *
     * @param file 오디오 파일 (mp3, wav, ogg 등)
     * @return 변환된 텍스트, 실패 시 null
     */
    public String transcribeAudio(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename();
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);
            body.add("model", whisperModel);
            body.add("language", "ko");

            String response = openAiRestClient.post()
                    .uri("/audio/transcriptions")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(response).path("text").asText();
        } catch (Exception e) {
            log.warn("Whisper 음성 변환 실패 [{}]: {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    private String extractContent(String rawResponse) {
        try {
            return objectMapper.readTree(rawResponse)
                    .path("choices").get(0)
                    .path("message").path("content").asText();
        } catch (Exception e) {
            log.error("OpenAI 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
    }
}