package com.findmypet.common.exception.user;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String loginId) {
        super(
                "이미 사용 중인 아이디입니다: " + loginId,
                HttpStatus.CONFLICT,
                "USER_DUPLICATE"
        );
    }
}