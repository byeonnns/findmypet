package com.findmypet.domain.common;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "attachment")
@Entity
public class Attachment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private int sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType attachmentType;

    @Column(nullable = false)
    private Long targetId;

    private String externalUploadId;

    @Column(nullable = false)
    private Long size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentStatus status;

    public static Attachment createInit(String filename, String contentType, int sortOrder, AttachmentType type, Long targetId, Long size) {
        return Attachment.builder()
                .filename(filename)
                .contentType(contentType)
                .sortOrder(sortOrder)
                .attachmentType(type)
                .targetId(targetId)
                .size(size)
                .url(null)
                .externalUploadId(null)
                .status(AttachmentStatus.INIT)
                .build();
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    // Presigned URL 방식으로 전환 시 사용 예정
    public void markUploading(String uploadId) {
        this.externalUploadId = uploadId;
        this.status = AttachmentStatus.UPLOADING;
    }

    public void markCompleted(String finalUrl) {
        this.url = finalUrl;
        this.status = AttachmentStatus.COMPLETED;
    }

    public void markFailed() {
        this.status = AttachmentStatus.FAILED;
    }

    public void markDeleted() {
        this.status = AttachmentStatus.DELETED;
    }


}
