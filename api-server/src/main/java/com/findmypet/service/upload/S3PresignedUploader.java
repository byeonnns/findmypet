package com.findmypet.service.upload;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@RequiredArgsConstructor
@Service
public class S3PresignedUploader {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    public void deleteFile(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    private String extractKeyFromUrl(String url) {
        String prefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
        if (!url.startsWith(prefix)) {
            throw new IllegalArgumentException("잘못된 S3 URL입니다: " + url);
        }
        return url.substring(prefix.length());
    }
}
