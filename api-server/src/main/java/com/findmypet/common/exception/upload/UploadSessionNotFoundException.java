package com.findmypet.common.exception.upload;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class UploadSessionNotFoundException extends BusinessException {
    public UploadSessionNotFoundException(String uploadId) {
        super("업로드 세션을 찾을 수 없습니다: " + uploadId,
                HttpStatus.NOT_FOUND,
                "UPLOAD_SESSION_NOT_FOUND");
    }
}