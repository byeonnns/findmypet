package com.findmypet.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUploadResponse {
    private String uploadId;
    private String presignedUrl;
    private String finalUrl;
}
