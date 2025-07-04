package com.findmypet.domain.common;

import com.findmypet.domain.inquiry.InquiryMessage;
import com.findmypet.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "attachment")
@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_message_id")
    private InquiryMessage inquiryMessage;

    @Column(nullable = false)
    private String url; // S3 스토리지 경로

    @Column(nullable = false)
    private int sortOrder; // 이미지 순서
}
