package com.findmypet.controller;

import com.findmypet.common.exception.general.PermissionDeniedException;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.message.AddMessageRequest;
import com.findmypet.dto.request.message.CreateMessageThreadRequest;
import com.findmypet.dto.response.MessageThreadDetailResponse;
import com.findmypet.dto.response.MessageResponse;
import com.findmypet.dto.response.MessageThreadResponse;
import com.findmypet.service.MessageService;
import com.findmypet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    private User getSessionUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new PermissionDeniedException("로그인한 사용자만 접근할 수 있습니다.");
        }
        return userService.findById(userId);
    }

    /**
     * 문의 스레드 생성
     * - 첨부파일 처리는 별도 presigned API 호출로 분리
     */
    @PostMapping
    public ResponseEntity<MessageThreadResponse> createMessageThread(@RequestBody CreateMessageThreadRequest request, HttpServletRequest httpRequest) {
        User sender = getSessionUser(httpRequest);
        MessageThreadResponse response = messageService.createMessageThread(request, sender);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 보낸 문의 목록 조회
     */
    @GetMapping("/sent")
    public ResponseEntity<Page<MessageThreadResponse>> getSentMessageThreads(Pageable pageable, HttpServletRequest request) {
        User sender = getSessionUser(request);
        Page<MessageThreadResponse> page = messageService.getSentMessageThreads(sender, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 받은 문의 목록 조회
     */
    @GetMapping("/received")
    public ResponseEntity<Page<MessageThreadResponse>> getReceivedMessageThreads(Pageable pageable, HttpServletRequest request) {
        User receiver = getSessionUser(request);
        Page<MessageThreadResponse> page = messageService.getReceivedMessageThreads(receiver, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * 특정 문의 스레드 상세 조회
     * - 메시지별 첨부파일은 Service에서 AttachmentRepository 통해 로딩
     */
    @GetMapping("/{threadId}")
    public ResponseEntity<MessageThreadDetailResponse> getMessageThreadDetail(@PathVariable Long threadId, HttpServletRequest request) {
        User user = getSessionUser(request);
        MessageThreadDetailResponse detail = messageService.getMessageThreadDetail(threadId, user);
        return ResponseEntity.ok(detail);
    }

    /**
     * 문의 스레드 삭제
     */
    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long threadId, HttpServletRequest request) {
        User user = getSessionUser(request);
        messageService.deleteMessageThread(threadId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * 스레드에 메시지 추가
     */
    @PostMapping("/{threadId}/messages")
    public ResponseEntity<MessageResponse> addMessage(@PathVariable Long threadId, @RequestBody AddMessageRequest request, HttpServletRequest requestObj) {
        User writer = getSessionUser(requestObj);
        MessageResponse response = messageService.addMessage(threadId, request, writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
