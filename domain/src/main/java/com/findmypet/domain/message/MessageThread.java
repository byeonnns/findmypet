package com.findmypet.domain.message;

import com.findmypet.domain.common.BaseTimeEntity;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "message_threads")
public class MessageThread extends BaseTimeEntity {

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
    private MessageThreadStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public static MessageThread create(Post post, User sender, User receiver) {
        return MessageThread.builder()
                .post(post)
                .sender(sender)
                .receiver(receiver)
                .status(MessageThreadStatus.PENDING)
                .build();
    }

    public void delete() {
        if (this.isDeleted) {
            throw new IllegalStateException("이미 삭제된 문의입니다.");
        }
        this.isDeleted = true;
    }

    public void addMessage(Message msg) {
        if (this.isDeleted) {
            throw new IllegalStateException("삭제된 문의에는 메시지를 추가할 수 없습니다.");
        }

        msg.setMessageThread(this);

        if (msg.getWriter().equals(this.receiver)) {
            this.status = MessageThreadStatus.ANSWERED;
        } else if (msg.getWriter().equals(this.sender)) {
            this.status = MessageThreadStatus.PENDING;
        }
    }
}
