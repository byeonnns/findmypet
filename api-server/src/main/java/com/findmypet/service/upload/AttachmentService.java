package com.findmypet.service.upload;

import com.findmypet.config.storage.StorageProperties;
import com.findmypet.domain.common.*;
import com.findmypet.dto.request.InitiateUploadRequest;
import com.findmypet.dto.response.PresignedUploadResponse;
import com.findmypet.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final S3PresignedUrlGenerator presignedUrlGenerator;
    private final StorageProperties storageProperties;

    /**
     * presigned URL 발급 + 첨부파일 메타데이터 INIT 상태로 저장
     */
    public PresignedUploadResponse initiateUpload(InitiateUploadRequest request, Long userId) {
        // 사이즈 검사
        validateFileSize(request.getSize());

        // 사용량 검사
        validateUserQuota(request.getSize(), userId);

        // UUID 기반 uploadId 생성
        String uploadId = UUID.randomUUID().toString();
        String fileKey = request.getAttachmentType().getFolder() + "/" + uploadId + "_" + request.getFilename();

        // presigned URL 생성
        String presignedUrl = presignedUrlGenerator.generatePutUrl(fileKey, request.getContentType());
        String finalUrl = presignedUrlGenerator.buildFinalUrl(fileKey);

        // INIT 상태로 메타 저장
        Attachment attachment = Attachment.builder()
                .filename(request.getFilename())
                .contentType(request.getContentType())
                .url(finalUrl)
                .sortOrder(0)
                .attachmentType(request.getAttachmentType())
                .targetId(request.getTargetId())
                .externalUploadId(uploadId)
                .size(request.getSize())
                .status(AttachmentStatus.INIT)
                .build();

        attachmentRepository.save(attachment);

        return new PresignedUploadResponse(uploadId, presignedUrl, finalUrl);
    }

    public void completeUpload(String uploadId) {
        Attachment attachment = attachmentRepository.findByExternalUploadId(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("업로드 ID를 찾을 수 없습니다: " + uploadId));

        attachment.markCompleted(attachment.getUrl()); // 이미 저장된 최종 URL
        attachmentRepository.save(attachment);
    }

    public void cancelUpload(String uploadId) {
        Attachment attachment = attachmentRepository.findByExternalUploadId(uploadId)
                .orElseThrow(() -> new IllegalArgumentException("업로드 ID를 찾을 수 없습니다: " + uploadId));

        attachment.markCanceled();
        attachmentRepository.save(attachment);
    }

    private void validateFileSize(long size) {
        long max = storageProperties.getMaxFileSize().toBytes();
        if (size > max) {
            throw new IllegalArgumentException("파일 크기가 허용된 최대 크기(" + max + "B)를 초과했습니다.");
        }
    }

    private void validateUserQuota(long size, Long userId) {
        long used = attachmentRepository.sumDoneSizeByUser(userId).orElse(0L);
        long limit = storageProperties.getUserQuota().toBytes();
        if (used + size > limit) {
            throw new IllegalStateException("사용자 저장 용량 초과: 사용량 = " + used + ", 한도 = " + limit);
        }
    }
}
