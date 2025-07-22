package com.findmypet.service.upload;

import com.findmypet.config.storage.StorageProperties;
import com.findmypet.common.exception.upload.FileSizeExceededException;
import com.findmypet.common.exception.upload.NoFileToUploadException;
import com.findmypet.common.exception.upload.StorageQuotaExceededException;
import com.findmypet.common.exception.upload.S3UrlGenerationException;
import com.findmypet.common.exception.upload.UploadSessionNotFoundException;
import com.findmypet.common.exception.user.UserNotFoundException;
import com.findmypet.domain.common.Attachment;
import com.findmypet.dto.request.upload.InitiateUploadRequest;
import com.findmypet.dto.response.PresignedUploadResponse;
import com.findmypet.repository.AttachmentRepository;
import com.findmypet.repository.UserRepository;
import com.findmypet.storage.PresignedUrlGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final StorageProperties storageProperties;
    private final PresignedUrlGenerator presignedUrlGenerator;
    private final S3PresignedUploader s3PresignedUploader;

    /**
     * 업로드 시작: presigned URL 생성 & Attachment 엔티티 초기 저장
     */
    @Transactional
    public PresignedUploadResponse initiateUpload(InitiateUploadRequest req, Long userId) {
        // 1) 파일 리스트 유효성 검사
        if (req.getFiles() == null || req.getFiles().isEmpty()) {
            throw new NoFileToUploadException();
        }
        // 2) 사용자 존재 여부 검증
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 3) 업로드 세션 식별자 생성
        String uploadId = UUID.randomUUID().toString();

        List<PresignedUploadResponse.UploadInfo> infos = new ArrayList<>();
        List<Attachment> toSave = new ArrayList<>();
        int sortOrder = 0;

        // 4) 파일별 presigned URL 생성 & Attachment 엔티티 저장 준비
        for (InitiateUploadRequest.FileRequest file : req.getFiles()) {
            long size = file.getSize();

            // 4-1) 파일 크기 제한 검사
            validateFileSize(size);
            // 4-2) 사용자 저장 용량 한도 검사
            validateUserQuota(size, userId);

            // 4-3) S3 객체 키 생성 (예: folder/targetId/uploadId/filename)
            String key = String.format("%s/%d/%s/%s",
                    req.getAttachmentType().getFolder(),
                    req.getTargetId(),
                    uploadId,
                    file.getFilename());

            // 4-4) Presigned URL 생성 및 최종 URL 빌드
            String uploadUrl;
            String finalUrl;
            try {
                uploadUrl = presignedUrlGenerator.generatePutUrl(key, file.getContentType());
                finalUrl  = presignedUrlGenerator.buildFinalUrl(key);
            } catch (Exception e) {
                throw new S3UrlGenerationException(key, e.getMessage());
            }

            // 4-5) Attachment 엔티티 초기 상태로 생성
            Attachment attach = Attachment.init(
                    file.getFilename(),
                    file.getContentType(),
                    sortOrder++,
                    req.getAttachmentType(),
                    req.getTargetId(),
                    file.getSize(),
                    uploadId,
                    finalUrl
            );
            toSave.add(attach);
            infos.add(new PresignedUploadResponse.UploadInfo(key, uploadUrl, null));
        }

        // 5) DB에 한 번에 저장
        attachmentRepository.saveAll(toSave);

        return new PresignedUploadResponse(uploadId, infos);
    }

    /**
     * 업로드 완료 처리: externalUploadId 로 묶인 Attachment 들의 URL(markCompleted) 반영
     */
    @Transactional
    public void completeUpload(String uploadId) {
        List<Attachment> attachments = attachmentRepository.findAllByExternalUploadId(uploadId);
        if (attachments.isEmpty()) {
            throw new UploadSessionNotFoundException(uploadId);
        }

        for (Attachment attachment : attachments) {
            String url = attachment.getUrl();
            if (url == null) {
                String key = String.format("%s/%d/%s/%s",
                        attachment.getAttachmentType().getFolder(),
                        attachment.getTargetId(),
                        uploadId,
                        attachment.getFilename());
                url = presignedUrlGenerator.buildFinalUrl(key);
            }
            attachment.markCompleted(url);
        }
        attachmentRepository.saveAll(attachments);
    }

    /**
     * 업로드 취소 처리: externalUploadId 로 묶인 Attachment 들을 canceled 표시
     */
    @Transactional
    public void cancelUpload(String uploadId) {
        List<Attachment> attachments = attachmentRepository.findAllByExternalUploadId(uploadId);
        if (attachments.isEmpty()) {
            throw new UploadSessionNotFoundException(uploadId);
        }

        for (Attachment attachment : attachments) {
            attachment.markCanceled();
        }
        attachmentRepository.saveAll(attachments);
    }

    @Transactional
    public void deleteFile(Long attachmentId, String fileUrl) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("첨부파일을 찾을 수 없습니다. id=" + attachmentId));

        s3PresignedUploader.deleteFile(fileUrl);

        attachmentRepository.delete(attachment);
    }

    private void validateFileSize(long size) {
        long max = storageProperties.getMaxFileSize().toBytes();
        if (size > max) {
            throw new FileSizeExceededException(max);
        }
    }

    private void validateUserQuota(long size, Long userId) {
        long used  = attachmentRepository.sumDoneSizeByUser(userId).orElse(0L);
        long limit = storageProperties.getUserQuota().toBytes();
        if (used + size > limit) {
            throw new StorageQuotaExceededException(used, limit);
        }
    }
}
