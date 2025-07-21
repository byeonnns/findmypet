package com.findmypet.common.exception.user;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException(String loginId) {
        super(
                "잘못된 비밀번호입니다: " + loginId,
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }
}