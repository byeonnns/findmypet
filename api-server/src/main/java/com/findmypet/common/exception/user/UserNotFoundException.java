package com.findmypet.common.exception.user;

import com.findmypet.common.exception.core.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String loginId) {
        super(
                "해당 유저를 찾을 수 없습니다. id=" + loginId,
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }

    public UserNotFoundException(Long id) {
        super(
                "해당 유저를 찾을 수 없습니다. id=" + id,
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }
}