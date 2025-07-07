package com.findmypet.domain.common;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Table(name = "attachment")
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url; // S3 스토리지 경로

    @Column(nullable = false)
    private int sortOrder; // 이미지 순서

    @Enumerated(EnumType.STRING)
    private AttachmentType attachmentType;

    private Long targetId;

    public Attachment(String url, int sortOrder, AttachmentType attachmentType, Long targetId) {
        this.url = url;
        this.sortOrder = sortOrder;
        this.attachmentType = attachmentType;
        this.targetId = targetId;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
