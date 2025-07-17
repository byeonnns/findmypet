package com.findmypet.repository;

import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType attachmentType, Long targetId);

    @Query("SELECT SUM(a.size) FROM Attachment a " +
            "WHERE a.status = 'DONE' AND a.targetId = :userId")
    Optional<Long> sumDoneSizeByUser(@Param("userId") Long userId);

    List<Attachment> findByAttachmentTypeAndTargetId(AttachmentType attachmentType, Long postId);
}