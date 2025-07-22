package com.findmypet.common.exception.upload;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class FileSizeExceededException extends BusinessException {
    public FileSizeExceededException(long maxSize) {
        super("파일 크기가 허용된 최대 크기(" + maxSize + "B)를 초과했습니다.",
                HttpStatus.BAD_REQUEST,
                "UPLOAD_FILE_TOO_LARGE");
    }
}