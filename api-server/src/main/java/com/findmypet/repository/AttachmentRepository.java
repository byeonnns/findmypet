package com.findmypet.repository;

import com.findmypet.domain.common.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}