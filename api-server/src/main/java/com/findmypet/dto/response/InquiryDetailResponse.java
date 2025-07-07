package com.findmypet.dto.response;

import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.inquiry.InquiryMessage;
import com.findmypet.domain.inquiry.InquiryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryDetailResponse {
    private Long inquiryId;
    private Long postId;
    private Long senderId;
    private Long receiverId;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InquiryMessageResponse> messages;

    public static InquiryDetailResponse from(Inquiry inquiry, List<InquiryMessage> messages) {
        List<InquiryMessageResponse> messageResponses = messages.stream()
                .map(InquiryMessageResponse::from)
                .collect(Collectors.toList());

        return new InquiryDetailResponse(
                inquiry.getId(),
                inquiry.getPost().getId(),
                inquiry.getSender().getId(),
                inquiry.getReceiver().getId(),
                inquiry.getStatus(),
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt(),
                messageResponses
        );
    }
}
