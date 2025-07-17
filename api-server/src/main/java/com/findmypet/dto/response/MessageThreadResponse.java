package com.findmypet.dto.response;

import com.findmypet.domain.inquiry.MessageThread;
import com.findmypet.domain.inquiry.MessageThreadStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageThreadResponse {

    private Long messageThreadId;
    private Long postId;
    private Long senderId;
    private Long receiverId;
    private MessageThreadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MessageThreadResponse from(MessageThread messageThread) {
        return new MessageThreadResponse(
                messageThread.getId(),
                messageThread.getPost().getId(),
                messageThread.getSender().getId(),
                messageThread.getReceiver().getId(),
                messageThread.getStatus(),
                messageThread.getCreatedAt(),
                messageThread.getUpdatedAt()
        );
    }
}
