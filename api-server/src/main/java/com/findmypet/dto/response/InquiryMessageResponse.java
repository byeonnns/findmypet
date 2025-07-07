package com.findmypet.dto.response;

import com.findmypet.domain.inquiry.InquiryMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InquiryMessageResponse {
    private Long messageId;
    private Long inquiryId;
    private Long writerId;
    private String content;
    private LocalDateTime createdAt;

    public static InquiryMessageResponse from(InquiryMessage message) {
        return new InquiryMessageResponse(
                message.getId(),
                message.getInquiry().getId(),
                message.getWriter().getId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}