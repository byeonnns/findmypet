package com.findmypet.domain.inquiry;

import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InquiryStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    public static Inquiry create(Post post, User sender, User receiver) {
        return Inquiry.builder()
                .post(post)
                .sender(sender)
                .receiver(receiver)
                .status(InquiryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void changeStatusToAnswered() {
        if (this.status != InquiryStatus.PENDING) {
            throw new IllegalStateException("이미 답변이 완료된 문의입니다.");
        }
        this.status = InquiryStatus.ANSWERED;
        stampUpdated();
    }

    public void stampUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        if (this.isDeleted) {
            throw new IllegalStateException("이미 삭제된 문의입니다.");
        }
        this.isDeleted = true;
        stampUpdated();
    }

    public boolean isPending() {
        return this.status == InquiryStatus.PENDING;
    }

    public boolean isAnswered() {
        return this.status == InquiryStatus.ANSWERED;
    }
}
