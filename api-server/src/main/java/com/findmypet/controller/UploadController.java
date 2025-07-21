package com.findmypet.controller;

import com.findmypet.dto.request.upload.CancelUploadRequest;
import com.findmypet.dto.request.upload.InitiateUploadRequest;
import com.findmypet.dto.request.upload.CompleteUploadRequest;
import com.findmypet.dto.response.PresignedUploadResponse;
import com.findmypet.service.upload.AttachmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final AttachmentService attachmentService;

    @PostMapping("/initiate")
    public ResponseEntity<PresignedUploadResponse> initiateUpload(@RequestBody InitiateUploadRequest request, HttpServletRequest requestContext) {
        Long userId = (Long) requestContext.getAttribute(SESSION_USER_ID);
        PresignedUploadResponse response = attachmentService.initiateUpload(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<Void> completeUpload(@RequestBody CompleteUploadRequest request) {
        attachmentService.completeUpload(request.getUploadId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelUpload(@RequestBody CancelUploadRequest request) {
        attachmentService.cancelUpload(request.getUploadId());
        return ResponseEntity.ok().build();
    }
}
