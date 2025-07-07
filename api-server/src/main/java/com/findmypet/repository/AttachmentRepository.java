package com.findmypet.repository;

import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType attachmentType, Long targetId);
    void deleteByAttachmentTypeAndTargetId(AttachmentType attachmentType, Long targetId);
}