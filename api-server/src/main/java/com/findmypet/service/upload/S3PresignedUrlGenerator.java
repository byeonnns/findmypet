package com.findmypet.service.upload;

import com.findmypet.storage.PresignedUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3PresignedUrlGenerator implements PresignedUrlGenerator {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * 업로드용 Presigned PUT URL 생성
     */
    @Override
    public String generatePutUrl(String fileKey, String contentType) {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .putObjectRequest(putReq)
                .signatureDuration(Duration.ofMinutes(10))
                .build();

        URL url = s3Presigner.presignPutObject(presignReq).url();
        return url.toString();
    }

    /**
     * 접근 가능한 URL 생성 (실제 S3 URL)
     */
    @Override
    public String buildFinalUrl(String fileKey) {
        return "https://" + bucket + ".s3.amazonaws.com/" + fileKey;
    }
}
