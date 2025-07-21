package com.findmypet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PresignedUploadResponse {
    private String uploadId;
    private List<UploadInfo> uploads;   // 멀티 파일 정보 리스트

    @Getter
    @AllArgsConstructor
    public static class UploadInfo {
        private String key;              // S3 객체 키
        private String presignedUrl;     // 업로드할 presigned URL
        private String finalUrl;         // 업로드 완료 후 접근할 최종 URL (선택사항)
    }
}
