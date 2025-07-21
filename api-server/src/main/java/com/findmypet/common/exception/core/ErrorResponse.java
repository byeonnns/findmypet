package com.findmypet.common.exception.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;
    private final List<FieldViolation> violations;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class FieldViolation {
        private final String field;
        private final String reason;
    }
}
