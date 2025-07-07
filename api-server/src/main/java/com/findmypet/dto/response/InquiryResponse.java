package com.findmypet.dto.response;

import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.inquiry.InquiryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryResponse {

    private Long inquiryId;
    private Long postId;
    private Long senderId;
    private Long receiverId;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InquiryResponse from(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getPost().getId(),
                inquiry.getSender().getId(),
                inquiry.getReceiver().getId(),
                inquiry.getStatus(),
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt()
        );
    }
}
