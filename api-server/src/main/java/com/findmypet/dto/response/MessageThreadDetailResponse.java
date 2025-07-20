package com.findmypet.dto.response;

import com.findmypet.domain.message.MessageThread;
import com.findmypet.domain.message.Message;
import com.findmypet.domain.message.MessageThreadStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class MessageThreadDetailResponse {
    private Long inquiryId;
    private Long postId;
    private Long senderId;
    private Long receiverId;
    private MessageThreadStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageResponse> messages;

    public static MessageThreadDetailResponse from(MessageThread messageThread, List<Message> messages) {
        List<MessageResponse> messageResponses = messages.stream()
                .map(MessageResponse::from)
                .collect(Collectors.toList());

        return new MessageThreadDetailResponse(
                messageThread.getId(),
                messageThread.getPost().getId(),
                messageThread.getSender().getId(),
                messageThread.getReceiver().getId(),
                messageThread.getStatus(),
                messageThread.getCreatedAt(),
                messageThread.getUpdatedAt(),
                messageResponses
        );
    }
}
