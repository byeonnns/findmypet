package com.findmypet.common.exception.upload;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class S3UrlGenerationException extends BusinessException {
    public S3UrlGenerationException(String key, String message) {
        super("Presigned URL 생성 실패: key=" + key + ", error=" + message,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "S3_URL_GENERATION_FAILED");
    }
}