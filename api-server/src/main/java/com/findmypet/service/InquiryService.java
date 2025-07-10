package com.findmypet.service;

import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.common.exception.ResourceNotFoundException;
import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.inquiry.InquiryMessage;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import com.findmypet.dto.notification.NotificationEvent;
import com.findmypet.dto.notification.NotificationType;
import com.findmypet.dto.request.CreateInquiryMessageRequest;
import com.findmypet.dto.request.CreateInquiryRequest;
import com.findmypet.dto.response.InquiryDetailResponse;
import com.findmypet.dto.response.InquiryMessageResponse;
import com.findmypet.dto.response.InquiryResponse;
import com.findmypet.notification.NotificationMessageBuilder;
import com.findmypet.notification.NotificationPublisher;
import com.findmypet.repository.AttachmentRepository;
import com.findmypet.repository.InquiryMessageRepository;
import com.findmypet.repository.InquiryRepository;
import com.findmypet.repository.PostRepository;
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
@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository inquiryMessageRepository;
    private final PostRepository postRepository;
    private final AttachmentRepository attachmentRepository;
    private final NotificationPublisher notificationPublisher;
    private final S3Uploader s3Uploader;

    public InquiryService(InquiryRepository inquiryRepository, InquiryMessageRepository inquiryMessageRepository, AttachmentRepository attachmentRepository,
                          PostRepository postRepository, NotificationPublisher notificationPublisher, S3Uploader s3Uploader) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryMessageRepository = inquiryMessageRepository;
        this.postRepository = postRepository;
        this.attachmentRepository = attachmentRepository;
        this.notificationPublisher = notificationPublisher;
        this.s3Uploader = s3Uploader;
    }

    @Transactional
    public InquiryResponse createInquiry(CreateInquiryRequest request, List<MultipartFile> attachments, User sender) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. id : " + request.getPostId()));

        Inquiry inquiry = Inquiry.create(post, sender, post.getWriter());
        inquiryRepository.save(inquiry);

        // ✅ 첨부파일 업로드 & 저장
        if (attachments != null && !attachments.isEmpty()) {
            List<Attachment> attachmentEntities = new ArrayList<>();

            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile file = attachments.get(i);
                try {
                    String url = s3Uploader.upload(file, "inquiries");

                    Attachment attachment = Attachment.builder()
                            .url(url)
                            .sortOrder(i)
                            .attachmentType(AttachmentType.INQUIRY_MESSAGE)
                            .targetId(inquiry.getId())
                            .build();
                    attachmentEntities.add(attachment);
                } catch (IOException e) {
                    log.error("[S3 업로드 실패 - 문의 첨부] 파일명: {}, 이유: {}", file.getOriginalFilename(), e.getMessage(), e);
                    // 실패한 파일은 무시
                }
            }

            if (!attachmentEntities.isEmpty()) {
                attachmentRepository.saveAll(attachmentEntities);
                log.info("[문의 첨부파일 저장] inquiryId = {}, count = {}", inquiry.getId(), attachmentEntities.size());
            }
        }

        // 알림 이벤트 발행
        String message = NotificationMessageBuilder.buildInquiryCreatedMessage(sender.getName());

        notificationPublisher.publish(
                NotificationEvent.of(
                        post.getWriter().getId().toString(),
                        NotificationType.INQUIRY_CREATED,
                        message
                )
        );

        log.info("[문의 생성] inquiryId={}", inquiry.getId());
        return InquiryResponse.from(inquiry);
    }

    public Page<InquiryResponse> getSentInquiries(User sender, Pageable pageable) {
        Page<InquiryResponse> page = inquiryRepository.findAllBySenderAndIsDeletedFalse(sender, pageable)
                .map(InquiryResponse::from);
        log.debug("[보낸 문의 목록 조회] senderId = {}, size = {}", sender.getId(), page.getSize());
        return page;
    }

    public Page<InquiryResponse> getReceivedInquiries(User receiver, Pageable pageable) {
        Page<InquiryResponse> page = inquiryRepository.findAllByReceiverAndIsDeletedFalse(receiver, pageable)
                .map(InquiryResponse::from);
        log.debug("[받은 문의 목록 조회] receiverId={}, size={}", receiver.getId(), page.getSize());
        return page;
    }

    @Transactional
    public InquiryDetailResponse getInquiryDetail(Long inquiryId, User user) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("문의를 찾을 수 없습니다. id=" + inquiryId));

        if (!user.equals(inquiry.getSender()) && !user.equals(inquiry.getReceiver())) {
            log.warn("[권한 거부] inquiryId = {}, userId = {}", inquiryId, user.getId());
            throw new PermissionDeniedException("해당 문의를 조회할 권한이 없습니다.");
        }

        List<InquiryMessage> messages = inquiryMessageRepository.findAllByInquiryOrderByCreatedAtAsc(inquiry);

        return InquiryDetailResponse.from(inquiry, messages);
    }

    @Transactional
    public void deleteInquiry(Long inquiryId, User user) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("문의 쓰레드를 찾을 수 없습니다. id=" + inquiryId));

        if (!user.equals(inquiry.getSender()) && !user.equals(inquiry.getReceiver())) {
            log.warn("[권한 거부] 문의 삭제 거절 : inquiryId = {}, userId = {}", inquiryId, user.getId());
            throw new PermissionDeniedException("삭제 권한이 없습니다.");
        }

        inquiry.delete();
        log.info("[문의 삭제] inquiryId = {}", inquiryId);
    }

    @Transactional
    public InquiryMessageResponse addMessage(Long inquiryId, CreateInquiryMessageRequest request, User writer) {
        Inquiry inquiry = inquiryRepository.findByIdAndIsDeletedFalse(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("문의를 찾을 수 없습니다. id=" + inquiryId));

        InquiryMessage message = InquiryMessage.create(inquiry, writer, request.getContent());
        inquiry.addMessage(message);
        inquiryMessageRepository.save(message);

        log.info("[문의 메시지 추가] messageId = {}, inquiryId = {}", message.getId(), inquiryId);

        // 알림 이벤트 발행
        User recipient = resolveRecipient(inquiry, writer);
        String messageText = NotificationMessageBuilder.buildInquiryReplyMessage(writer.getName());

        notificationPublisher.publish(
                NotificationEvent.of(
                        recipient.getId().toString(),
                        NotificationType.INQUIRY_MESSAGE_REPLIED,
                        messageText
                )
        );

        return InquiryMessageResponse.from(message);
    }

    // 알림 수신자 식별용 메서드
    private User resolveRecipient(Inquiry inquiry, User writer) {
        if (inquiry.getSender().getId().equals(writer.getId())) {
            return inquiry.getPost().getWriter();  // 작성자가 상대방
        } else {
            return inquiry.getSender();  // 문의 보낸 사람이 상대방
        }
    }
}
