package com.newbranch.backend.infra.s3;

import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Uploader {

    public static final Set<String> FILE_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public static final Set<String> LECTURE_CONTENT_TYPES = Set.of(
            "audio/mpeg",
            "audio/wav",
            "audio/ogg"
    );

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String upload(MultipartFile file, String dirName, Set<String> allowedContentTypes) {
        // fix: Set.of(...)의 contains(null)은 NPE를 던져서 Content-Type 헤더가 없는 요청이
        // 500으로 노출됐음. null이면 곧바로 잘못된 파일 타입으로 취급 (siwon)
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
        // fix: 클라이언트가 보낸 원본 파일명을 그대로 키에 이어붙이면 "/", ".." 등을 넣어
        // 의도한 디렉터리 밖으로 객체를 쓰거나 덮어쓸 수 있어 경로 구분자를 제거 (siwon)
        String safeFilename = sanitizeFilename(file.getOriginalFilename());
        String key = dirName + "/" + UUID.randomUUID() + "_" + safeFilename;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return buildFileUrl(key);
    }

    public void delete(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    private String buildFileUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String extractKeyFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
    }

    private String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "file";
        }
        String nameOnly = originalFilename.replace("\\", "/");
        nameOnly = nameOnly.substring(nameOnly.lastIndexOf('/') + 1);
        return nameOnly.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
