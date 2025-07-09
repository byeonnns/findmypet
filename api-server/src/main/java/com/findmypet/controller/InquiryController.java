package com.findmypet.controller;

import com.findmypet.common.exception.PermissionDeniedException;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.CreateInquiryMessageRequest;
import com.findmypet.dto.request.CreateInquiryRequest;
import com.findmypet.dto.response.InquiryDetailResponse;
import com.findmypet.dto.response.InquiryMessageResponse;
import com.findmypet.dto.response.InquiryResponse;
import com.findmypet.service.InquiryService;
import com.findmypet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@Slf4j
@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserService userService;

    public InquiryController(InquiryService inquiryService, UserService userService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
    }

    private User getSessionUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new PermissionDeniedException("로그인한 사용자만 접근할 수 있습니다.");
        }
        return userService.findById(userId);
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(@RequestBody CreateInquiryRequest request, HttpServletRequest requestObj) {
        User sender = getSessionUser(requestObj);
        InquiryResponse response = inquiryService.createInquiry(request, sender);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<InquiryResponse>> getSentInquiries(Pageable pageable, HttpServletRequest request) {
        User sender = getSessionUser(request);
        Page<InquiryResponse> page = inquiryService.getSentInquiries(sender, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/received")
    public ResponseEntity<Page<InquiryResponse>> getReceivedInquiries(Pageable pageable, HttpServletRequest request) {
        User receiver = getSessionUser(request);
        Page<InquiryResponse> page = inquiryService.getReceivedInquiries(receiver, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDetailResponse> getInquiryDetail(@PathVariable Long inquiryId, HttpServletRequest request) {
        User user = getSessionUser(request);
        InquiryDetailResponse detail = inquiryService.getInquiryDetail(inquiryId, user);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long inquiryId, HttpServletRequest request) {
        User user = getSessionUser(request);
        inquiryService.deleteInquiry(inquiryId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{inquiryId}/messages")
    public ResponseEntity<InquiryMessageResponse> addMessage(
            @PathVariable Long inquiryId,
            @RequestBody CreateInquiryMessageRequest request,
            HttpServletRequest requestObj) {
        User writer = getSessionUser(requestObj);
        InquiryMessageResponse response = inquiryService.addMessage(inquiryId, request, writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
