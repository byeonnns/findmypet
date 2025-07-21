package com.findmypet.service;

import com.findmypet.common.exception.general.PermissionDeniedException;
import com.findmypet.common.exception.general.ResourceNotFoundException;
import com.findmypet.domain.message.MessageThread;
import com.findmypet.domain.message.Message;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import com.findmypet.dto.notification.NotificationEvent;
import com.findmypet.dto.notification.NotificationType;
import com.findmypet.dto.request.message.AddMessageRequest;
import com.findmypet.dto.request.message.CreateMessageThreadRequest;
import com.findmypet.dto.response.MessageThreadDetailResponse;
import com.findmypet.dto.response.MessageResponse;
import com.findmypet.dto.response.MessageThreadResponse;
import com.findmypet.notification.NotificationMessageBuilder;
import com.findmypet.notification.NotificationPublisher;
import com.findmypet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageThreadRepository messageThreadRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final NotificationPublisher notificationPublisher;

    /**
     * 문의 스레드 생성
     * - 첨부파일 처리는 presigned API로 분리
     */
    @Transactional
    public MessageThreadResponse createMessageThread(CreateMessageThreadRequest request, User sender) {
        // 1) 대상 게시글 조회
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        // 2) MessageThread 생성 및 저장
        MessageThread messageThread = MessageThread.create(post, sender, post.getWriter());
        messageThreadRepository.save(messageThread);

        // 3) 최초 메시지 저장
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            Message initialMsg = Message.create(messageThread, sender, request.getContent());
            messageRepository.save(initialMsg);
            messageThread.addMessage(initialMsg);
        }

        // 4) 알림 이벤트 발행
        String notificationMsg = NotificationMessageBuilder.buildInquiryCreatedMessage(sender.getName());
        notificationPublisher.publish(
                NotificationEvent.of(
                        post.getWriter().getId().toString(),
                        NotificationType.INQUIRY_CREATED,
                        notificationMsg
                )
        );

        log.info("[문의 생성] messageThreadId = {}", messageThread.getId());
        return MessageThreadResponse.from(messageThread);
    }

    /**
     * 보낸 문의 목록 조회
     */
    public Page<MessageThreadResponse> getSentMessageThreads(User sender, Pageable pageable) {
        Page<MessageThreadResponse> page = messageThreadRepository
                .findAllBySenderAndIsDeletedFalse(sender, pageable)
                .map(MessageThreadResponse::from);
        log.debug("[보낸 문의 목록 조회] senderId = {}, size = {}", sender.getId(), page.getSize());
        return page;
    }

    /**
     * 받은 문의 목록 조회
     */
    public Page<MessageThreadResponse> getReceivedMessageThreads(User receiver, Pageable pageable) {
        Page<MessageThreadResponse> page = messageThreadRepository
                .findAllByReceiverAndIsDeletedFalse(receiver, pageable)
                .map(MessageThreadResponse::from);
        log.debug("[받은 문의 목록 조회] receiverId = {}, size = {}", receiver.getId(), page.getSize());
        return page;
    }

    /**
     * 문의 스레드 상세 조회
     */
    @Transactional
    public MessageThreadDetailResponse getMessageThreadDetail(Long threadId, User user) {
        MessageThread messageThread = messageThreadRepository
                .findByIdAndIsDeletedFalse(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("문의를 찾을 수 없습니다."));

        if (!user.equals(messageThread.getSender()) && !user.equals(messageThread.getReceiver())) {
            log.warn("[권한 거부] threadId = {}, userId = {}", threadId, user.getId());
            throw new PermissionDeniedException("해당 문의를 조회할 권한이 없습니다.");
        }

        List<Message> messages = messageRepository
                .findAllByMessageThreadOrderByCreatedAtAsc(messageThread);

        return MessageThreadDetailResponse.from(messageThread, messages);
    }

    /**
     * 문의 스레드 삭제
     */
    @Transactional
    public void deleteMessageThread(Long threadId, User user) {
        MessageThread messageThread = messageThreadRepository
                .findByIdAndIsDeletedFalse(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("문의 쓰레드를 찾을 수 없습니다."));

        if (!user.equals(messageThread.getSender()) && !user.equals(messageThread.getReceiver())) {
            log.warn("[권한 거부] 문의 삭제 거절 : threadId = {}, userId = {}", threadId, user.getId());
            throw new PermissionDeniedException("삭제 권한이 없습니다.");
        }

        messageThread.delete();
        log.info("[문의 삭제] threadId = {}", threadId);
    }

    /**
     * 스레드에 메시지 추가
     * - 첨부파일 처리는 presigned API로 분리
     */
    @Transactional
    public MessageResponse addMessage(Long threadId, AddMessageRequest request, User writer) {
        // 1) 스레드 조회
        MessageThread messageThread = messageThreadRepository
                .findByIdAndIsDeletedFalse(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("문의를 찾을 수 없습니다."));

        // 2) 메시지 생성 및 저장
        Message message = Message.create(messageThread, writer, request.getContent());
        messageRepository.save(message);
        messageThread.addMessage(message);

        log.info("[문의 메시지 추가] messageId = {}, threadId = {}", message.getId(), threadId);

        // 3) 알림 이벤트 발행
        User recipient = resolveRecipient(messageThread, writer);
        String notificationMsg = NotificationMessageBuilder.buildInquiryReplyMessage(writer.getName());
        notificationPublisher.publish(
                NotificationEvent.of(
                        recipient.getId().toString(),
                        NotificationType.INQUIRY_MESSAGE_REPLIED,
                        notificationMsg
                )
        );

        return MessageResponse.from(message);
    }

    // 알림 수신자 식별용 메서드
    private User resolveRecipient(MessageThread messageThread, User writer) {
        if (messageThread.getSender().getId().equals(writer.getId())) {
            return messageThread.getPost().getWriter();
        } else {
            return messageThread.getSender();
        }
    }
}
