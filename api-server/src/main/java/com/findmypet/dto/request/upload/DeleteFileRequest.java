package com.findmypet.dto.request.upload;

import lombok.Getter;

@Getter
public class DeleteFileRequest {
    private Long attachmentId;
    private String url; // S3 객체 키 추출용
}