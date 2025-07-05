package com.findmypet.dto.response;

import com.findmypet.domain.post.Post;
import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.domain.common.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private Long writerId;
    private PostType postType;
    private String title;
    private String location;
    private String description;
    private List<AttachmentResponse> attachments;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post, List<Attachment> attachments) {

        List<AttachmentResponse> dtoList = attachments.stream()
                .map(AttachmentResponse::from)
                .collect(Collectors.toList());

        return new PostResponse(
                post.getId(),
                post.getWriter().getId(),
                post.getPostType(),
                post.getTitle(),
                post.getLocation(),
                post.getDescription(),
                dtoList,
                post.getStatus(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}