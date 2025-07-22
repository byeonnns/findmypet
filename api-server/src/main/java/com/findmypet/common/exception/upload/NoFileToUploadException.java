package com.findmypet.common.exception.upload;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class NoFileToUploadException extends BusinessException {
    public NoFileToUploadException() {
        super("업로드할 파일이 없습니다.", HttpStatus.BAD_REQUEST, "UPLOAD_NO_FILES");
    }
}