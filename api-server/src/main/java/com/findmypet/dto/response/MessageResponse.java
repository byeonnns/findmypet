package com.findmypet.dto.response;

import com.findmypet.domain.inquiry.Message;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageResponse {
    private Long messageId;
    private Long messageThreadId;
    private Long writerId;
    private String content;
    private LocalDateTime createdAt;

    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getMessageThread().getId(),
                message.getWriter().getId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}