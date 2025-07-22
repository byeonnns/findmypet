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

    public static Attachment init(String filename, String contentType, int sortOrder, AttachmentType type, Long targetId, Long size, String uploadId, String url) {
        return Attachment.builder()
                .filename(filename)
                .contentType(contentType)
                .sortOrder(sortOrder)
                .attachmentType(type)
                .targetId(targetId)
                .size(size)
                .externalUploadId(uploadId)
                .url(url)
                .status(AttachmentStatus.UPLOADING)
                .build();
    }

    public void markDeleted() {
        this.status = AttachmentStatus.DELETED;
    }

    public void markCanceled() {
        this.status = AttachmentStatus.FAILED;
    }

    public void markCompleted(String finalUrl) {
        this.url = finalUrl;
        this.status = AttachmentStatus.COMPLETED;
    }

}
