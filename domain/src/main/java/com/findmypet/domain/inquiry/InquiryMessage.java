package com.findmypet.domain.inquiry;

import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 문의 대화의 개별 메시지를 나타내는 엔티티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "inquiry_messages")
public class InquiryMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 메시지 생성 시 검증 및 연관 엔티티 업데이트
     */
    public static InquiryMessage create(Inquiry inquiry, User writer, String content) {
        if (inquiry == null) {
            throw new IllegalArgumentException("Inquiry는 필수입니다.");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Author는 필수입니다.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용을 입력해주세요.");
        }

        InquiryMessage message = InquiryMessage.builder()
                .inquiry(inquiry)
                .writer(writer)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        // Inquiry의 updatedAt 갱신
        inquiry.stampUpdated();

        return message;
    }
}
