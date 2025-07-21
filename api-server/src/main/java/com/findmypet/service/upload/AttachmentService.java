package com.findmypet.service.upload;

import com.findmypet.config.storage.StorageProperties;
import com.findmypet.domain.common.*;
import com.findmypet.dto.request.InitiateUploadRequest;
import com.findmypet.dto.response.PresignedUploadResponse;
import com.findmypet.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final StorageProperties storageProperties;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    /**
     * presigned URL 발급 + 첨부파일 메타데이터 INIT 상태로 저장
     */
    @Transactional
    public PresignedUploadResponse initiateUpload(InitiateUploadRequest req) {
        // 1) 파일 리스트 유효성 검사
        if (req.getFiles() == null || req.getFiles().isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 2) 업로드 세션 식별자 생성
        String uploadId = UUID.randomUUID().toString();

        List<PresignedUploadResponse.UploadInfo> infos = new ArrayList<>();
        int sortOrder = 0;

        // 3) 파일별로 presigned URL 생성 & Attachment 엔티티 저장
        for (InitiateUploadRequest.FileRequest file : req.getFiles()) {
            // (가) S3 객체 키 생성 (예: folder/targetId/uploadId/filename)
            String key = String.format("%s/%d/%s/%s",
                    req.getAttachmentType().getFolder(),
                    req.getTargetId(),
                    uploadId,
                    file.getFilename()
            );

            // (나) presigned URL 생성
            URL url = s3Presigner.presignPutObject(p -> p
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(r -> r
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                    )
            ).url();

            String finalUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

            // (다) Attachment 엔티티 초기 상태로 생성 및 저장
            Attachment attach = Attachment.createInit(
                    file.getFilename(),
                    file.getContentType(),
                    sortOrder++,
                    req.getAttachmentType(),
                    req.getTargetId(),
                    file.getSize(),
                    uploadId,
                    finalUrl
            );
            attachmentRepository.save(attach);

            // (라) 응답용 DTO에 담기
            infos.add(new PresignedUploadResponse.UploadInfo(key, url.toString(), null));
        }

        // 4) uploadId 와 URL 리스트를 감싸서 반환
        return new PresignedUploadResponse(uploadId, infos);
    }

    public void completeUpload(String uploadId) {
        List<Attachment> attachments = attachmentRepository.findAllByExternalUploadId(uploadId);
        if (attachments.isEmpty()) {
            throw new IllegalArgumentException("업로드 ID를 찾을 수 없습니다: " + uploadId);
        }

        for (Attachment attachment : attachments) {
            // 이미 url이 존재하면 그대로 사용, 없으면 S3 URL 생성
            String finalUrl = attachment.getUrl();
            if (finalUrl == null) {
                String key = String.format("%s/%d/%s/%s",
                        attachment.getAttachmentType().getFolder(),  // 예: "posts"
                        attachment.getTargetId(),                   // 예: 15
                        attachment.getExternalUploadId(),           // UUID
                        attachment.getFilename()                    // 원본 파일명
                );
                finalUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
            }
            attachment.markCompleted(finalUrl);
        }
    }

    public void cancelUpload(String uploadId) {
        List<Attachment> attachments = attachmentRepository.findAllByExternalUploadId(uploadId);
        if (attachments.isEmpty()) {
            throw new IllegalArgumentException("업로드 ID를 찾을 수 없습니다: " + uploadId);
        }

        for (Attachment attachment : attachments) {
            attachment.markCanceled();
        }
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
