package com.findmypet.service;

import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.common.exception.ResourceNotFoundException;
import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import com.findmypet.domain.message.MessageThread;
import com.findmypet.domain.message.Message;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import com.findmypet.dto.notification.NotificationEvent;
import com.findmypet.dto.notification.NotificationType;
import com.findmypet.dto.request.AddMessageRequest;
import com.findmypet.dto.request.CreateMessageThreadRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageThreadRepository messageThreadRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final AttachmentRepository attachmentRepository;
    private final NotificationPublisher notificationPublisher;
    private final S3Uploader s3Uploader;

    @Transactional
    public MessageThreadResponse createMessageThread(CreateMessageThreadRequest request, List<MultipartFile> attachments, User sender) {
        // 1) MessageThread 생성 및 저장
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));

        MessageThread messageThread = MessageThread.create(post, sender, post.getWriter());
        messageThreadRepository.save(messageThread);

        // 2) 최초 메시지 저장
        Message initialMsg = null;
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            initialMsg = Message.create(messageThread, sender, request.getContent());
            messageRepository.save(initialMsg);
            messageThread.addMessage(initialMsg);
        }

        // 3) 첨부파일 업로드 & 저장
        if (initialMsg != null && attachments != null && !attachments.isEmpty()) {
            List<Attachment> attachmentEntities = new ArrayList<>();
            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile file = attachments.get(i);
                try {
                    String url = s3Uploader.upload(file, "messages");
                    Attachment attachment = Attachment.builder()
                            .url(url)
                            .sortOrder(i)
                            .attachmentType(AttachmentType.MESSAGE)
                            .targetId(initialMsg.getId())
                            .build();
                    attachmentEntities.add(attachment);
                } catch (IOException e) {
                    log.error("[S3 업로드 실패 - 문의 첨부] 파일명: {}, 이유: {}",
                            file.getOriginalFilename(), e.getMessage(), e);
                }
            }
            if (!attachmentEntities.isEmpty()) {
                attachmentRepository.saveAll(attachmentEntities);
                log.info("[문의 첨부파일 저장] inquiryMessageId = {}, count = {}",
                        initialMsg.getId(), attachmentEntities.size());
            }
        }

        // 4) 알림 이벤트 발행
        String message = NotificationMessageBuilder.buildInquiryCreatedMessage(sender.getName());
        notificationPublisher.publish(
                NotificationEvent.of(
                        post.getWriter().getId().toString(),
                        NotificationType.INQUIRY_CREATED,
                        message
                )
        );

        log.info("[문의 생성] messageThreadId = {}", messageThread.getId());
        return MessageThreadResponse.from(messageThread);
    }

    public Page<MessageThreadResponse> getSentMessageThreads(User sender, Pageable pageable) {
        Page<MessageThreadResponse> page = messageThreadRepository
                .findAllBySenderAndIsDeletedFalse(sender, pageable)
                .map(MessageThreadResponse::from);
        log.debug("[보낸 문의 목록 조회] senderId = {}, size = {}", sender.getId(), page.getSize());
        return page;
    }

    public Page<MessageThreadResponse> getReceivedMessageThreads(User receiver, Pageable pageable) {
        Page<MessageThreadResponse> page = messageThreadRepository
                .findAllByReceiverAndIsDeletedFalse(receiver, pageable)
                .map(MessageThreadResponse::from);
        log.debug("[받은 문의 목록 조회] receiverId = {}, size = {}", receiver.getId(), page.getSize());
        return page;
    }

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

    @Transactional
    public MessageResponse addMessage(Long threadId, AddMessageRequest request, List<MultipartFile> attachments, User writer) {
        MessageThread messageThread = messageThreadRepository
                .findByIdAndIsDeletedFalse(threadId)
                .orElseThrow(() -> new ResourceNotFoundException("문의를 찾을 수 없습니다."));

        Message message = Message.create(messageThread, writer, request.getContent());
        messageThread.addMessage(message);
        messageRepository.save(message);

        log.info("[문의 메시지 추가] messageId = {}, threadId = {}", message.getId(), threadId);

        // 첨부파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            List<Attachment> attachmentEntities = new ArrayList<>();
            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile file = attachments.get(i);
                try {
                    String url = s3Uploader.upload(file, "messages");

                    Attachment attachment = Attachment.builder()
                            .url(url)
                            .sortOrder(i)
                            .attachmentType(AttachmentType.MESSAGE)
                            .targetId(message.getId())
                            .build();
                    attachmentEntities.add(attachment);
                } catch (IOException e) {
                    log.error("[S3 업로드 실패 - 문의 메시지 첨부] 파일명 = {}, 이유 = {}", file.getOriginalFilename(), e.getMessage(), e);
                }
            }

            if (!attachmentEntities.isEmpty()) {
                attachmentRepository.saveAll(attachmentEntities);
                log.info("[문의 메시지 첨부 저장] messageId = {}, count = {}", message.getId(), attachmentEntities.size());
            }
        }

        // 알림 이벤트 발행
        User recipient = resolveRecipient(messageThread, writer);
        String messageText = NotificationMessageBuilder.buildInquiryReplyMessage(writer.getName());
        notificationPublisher.publish(
                NotificationEvent.of(
                        recipient.getId().toString(),
                        NotificationType.INQUIRY_MESSAGE_REPLIED,
                        messageText
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
