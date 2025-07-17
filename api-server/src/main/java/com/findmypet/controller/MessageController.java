package com.findmypet.controller;

import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.AddMessageRequest;
import com.findmypet.dto.request.CreateMessageThreadRequest;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<MessageThreadResponse> createMessageThread(@RequestPart("request") CreateMessageThreadRequest request, @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments, HttpServletRequest httpRequest) {
        User sender = getSessionUser(httpRequest);
        MessageThreadResponse response = messageService.createMessageThread(request, attachments, sender);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<MessageThreadResponse>> getSentMessageThreads(Pageable pageable, HttpServletRequest request) {
        User sender = getSessionUser(request);
        Page<MessageThreadResponse> page = messageService.getSentMessageThreads(sender, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/received")
    public ResponseEntity<Page<MessageThreadResponse>> getReceivedMessageThreads(Pageable pageable, HttpServletRequest request) {
        User receiver = getSessionUser(request);
        Page<MessageThreadResponse> page = messageService.getReceivedMessageThreads(receiver, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<MessageThreadDetailResponse> getMessageThreadDetail(@PathVariable Long threadId, HttpServletRequest request) {
        User user = getSessionUser(request);
        MessageThreadDetailResponse detail = messageService.getMessageThreadDetail(threadId, user);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long threadId, HttpServletRequest request) {
        User user = getSessionUser(request);
        messageService.deleteMessageThread(threadId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{threadId}/messages")
    public ResponseEntity<MessageResponse> addMessage(@PathVariable Long threadId, @RequestPart("request") AddMessageRequest request, @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments, HttpServletRequest requestObj) {
        User writer = getSessionUser(requestObj);
        MessageResponse response = messageService.addMessage(threadId, request, attachments, writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
