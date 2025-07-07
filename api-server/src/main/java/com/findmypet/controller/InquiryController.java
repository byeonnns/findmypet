package com.findmypet.controller;

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

@Slf4j
@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {
    private static final String SESSION_USER_ID = "USER_ID";

    private final InquiryService inquiryService;
    private final UserService userService;

    public InquiryController(InquiryService inquiryService, UserService userService) {
        this.inquiryService = inquiryService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(@RequestBody CreateInquiryRequest request, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User sender = userService.findById(userId);
        InquiryResponse response = inquiryService.createInquiry(request, sender);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<Page<InquiryResponse>> getSentInquiries(Pageable pageable, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User sender = userService.findById(userId);
        Page<InquiryResponse> page = inquiryService.getSentInquiries(sender, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/received")
    public ResponseEntity<Page<InquiryResponse>> getReceivedInquiries(Pageable pageable, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User receiver = userService.findById(userId);
        Page<InquiryResponse> page = inquiryService.getReceivedInquiries(receiver, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryDetailResponse> getInquiryDetail(@PathVariable Long inquiryId, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User user = userService.findById(userId);
        InquiryDetailResponse detail = inquiryService.getInquiryDetail(inquiryId, user);
        return ResponseEntity.ok(detail);
    }

    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable Long inquiryId, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User user = userService.findById(userId);
        inquiryService.deleteInquiry(inquiryId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{inquiryId}/messages")
    public ResponseEntity<InquiryMessageResponse> addMessage(@PathVariable Long inquiryId, @RequestBody CreateInquiryMessageRequest request, HttpServletRequest httpReq) {
        Long userId = (Long) httpReq.getAttribute(SESSION_USER_ID);
        User writer = userService.findById(userId);
        InquiryMessageResponse response = inquiryService.addMessage(inquiryId, request, writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
