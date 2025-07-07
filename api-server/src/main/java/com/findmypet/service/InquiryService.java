package com.findmypet.service;

import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.common.exception.ResourceNotFoundException;
import com.findmypet.domain.inquiry.Inquiry;
import com.findmypet.domain.inquiry.InquiryMessage;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.CreateInquiryMessageRequest;
import com.findmypet.dto.request.CreateInquiryRequest;
import com.findmypet.dto.response.InquiryDetailResponse;
import com.findmypet.dto.response.InquiryMessageResponse;
import com.findmypet.dto.response.InquiryResponse;
import com.findmypet.repository.InquiryMessageRepository;
import com.findmypet.repository.InquiryRepository;
import com.findmypet.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository inquiryMessageRepository;
    private final PostRepository postRepository;

    public InquiryService(InquiryRepository inquiryRepository, InquiryMessageRepository inquiryMessageRepository, PostRepository postRepository) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryMessageRepository = inquiryMessageRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public InquiryResponse createInquiry(CreateInquiryRequest request, User sender) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. id : " + request.getPostId()));

        Inquiry inquiry = Inquiry.create(post, sender, post.getWriter());
        inquiryRepository.save(inquiry);
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

        return InquiryMessageResponse.from(message);
    }
}
