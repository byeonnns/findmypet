package com.findmypet.domain.post;

import com.findmypet.domain.common.BaseTimeEntity;
import com.findmypet.domain.common.Pet;
import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "posts")
@Entity
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 글 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String location; // 분실 or 발견 장소

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostStatus status; // 미해결, 매칭중, 해결됨

    @Column(nullable = false)
    private Pet pet; // 반려동물 정보

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public static Post create(User writer, PostType postType, String title, String location, String description,
                              Pet pet) {
        return Post.builder()
                .writer(writer)
                .title(title)
                .postType(postType)
                .location(location)
                .description(description)
                .pet(pet)
                .status(PostStatus.UNRESOLVED)
                .build();
    }

    public void update(String title, String location, String description, Pet pet) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.pet = pet;
    }

    public void resolve() {
        if (this.status == PostStatus.RESOLVED) {
            throw new IllegalStateException("이미 해결된 게시글입니다.");
        }

        this.status = PostStatus.RESOLVED;
    }

    public void delete() {
        if (this.isDeleted) {
            throw new IllegalStateException("이미 삭제된 게시입니다.");
        }
        this.isDeleted = true;
    }
}
