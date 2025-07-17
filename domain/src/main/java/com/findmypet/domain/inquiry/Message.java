package com.findmypet.domain.inquiry;

import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_thread_id", nullable = false)
    private MessageThread messageThread;

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
    public static Message create(MessageThread messageThread, User writer, String content) {
        boolean isSender   = writer.equals(messageThread.getSender());
        boolean isReceiver = writer.equals(messageThread.getReceiver());
        if (!isSender && !isReceiver) {
            throw new IllegalArgumentException("메시지 작성자는 반드시 문의 작성자 또는 수신자여야 합니다.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }

        Message msg = new Message();
        msg.messageThread = messageThread;
        msg.writer       = writer;
        msg.content      = content;
        msg.createdAt    = LocalDateTime.now();

        messageThread.stampUpdated();

        return msg;
    }

    // 연관관계 설정용 setter, 외부에서 못 쓰도록 접근제어 좁힘
    void setMessageThread(MessageThread messageThread) {
        this.messageThread = messageThread;
    }
}
