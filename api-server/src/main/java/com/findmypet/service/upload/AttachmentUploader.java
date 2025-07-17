package com.findmypet.service.upload;

import com.findmypet.config.storage.StorageProperties;
import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import com.findmypet.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AttachmentUploader {

    private final S3Uploader s3Uploader;
    private final StorageProperties storageProperties;
    private final AttachmentRepository attachmentRepository;

    public Attachment upload(
            MultipartFile file,
            int sortOrder,
            AttachmentType attachmentType,
            Long targetId,
            Long userId
    ) {
        // 1) 파일 단위 크기 및 누적 용량 검사
        validateFileSize(file);
        validateUserQuota(file, userId);

        // 2) INIT 상태 메타데이터 생성
        Attachment attachment = Attachment.createInit(
                file.getOriginalFilename(),
                file.getContentType(),
                sortOrder,
                attachmentType,
                targetId,
                file.getSize()
        );

        // 3) INIT 상태 저장
        attachmentRepository.save(attachment);

        // 4) S3 업로드 시도 후 상태 변경
        try {
            String url = s3Uploader.upload(file, attachmentType.getFolder());
            attachment.markCompleted(url);
        } catch (IOException e) {
            attachment.markFailed();
            throw new RuntimeException("S3 업로드 실패", e);
        }

        // 5) 상태가 바뀐 attachment 저장
        return attachmentRepository.save(attachment);
    }

    private void validateFileSize(MultipartFile file) {
        long max = storageProperties.getMaxFileSize().toBytes();
        if (file.getSize() > max) {
            throw new IllegalArgumentException(
                    "파일 " + file.getOriginalFilename()
                            + " 의 크기가 허용된 최대 크기(" + max + "B)를 초과했습니다."
            );
        }
    }

    private void validateUserQuota(MultipartFile file, Long userId) {
        long used = attachmentRepository.sumDoneSizeByUser(userId).orElse(0L);
        long limit = storageProperties.getUserQuota().toBytes();

        if (used + file.getSize() > limit) {
            throw new IllegalStateException(
                    "사용자 저장 용량 초과: 사용량 = " + used + " / 한도 = " + limit
            );
        }
    }
}
