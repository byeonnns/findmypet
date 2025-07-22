package com.findmypet.common.exception.upload;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class StorageQuotaExceededException extends BusinessException {
    public StorageQuotaExceededException(long used, long limit) {
        super("사용자 저장 용량 초과: 사용량=" + used + "B, 한도=" + limit + "B",
                HttpStatus.CONFLICT,
                "UPLOAD_QUOTA_EXCEEDED");
    }
}