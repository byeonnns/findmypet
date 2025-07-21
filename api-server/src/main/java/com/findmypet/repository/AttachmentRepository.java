package com.findmypet.repository;

import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType attachmentType, Long targetId);

    List<Attachment> findByAttachmentTypeAndTargetId(AttachmentType attachmentType, Long postId);

    // uploadId로 조회 (presigned 기반)
    List<Attachment> findAllByExternalUploadId(String uploadId);

    // 특정 게시물/메시지의 첨부파일 삭제
    void deleteByAttachmentTypeAndTargetId(AttachmentType type, Long targetId);

    // 해당 사용자가 사용한 첨부파일 용량 확인
    @Query("SELECT SUM(a.size) FROM Attachment a " +
            "WHERE a.status = 'COMPLETED' AND a.attachmentType = 'POST' AND a.targetId IN " +
            "(SELECT p.id FROM Post p WHERE p.writer.id = :userId)")
    Optional<Long> sumDoneSizeByUser(Long userId);
}