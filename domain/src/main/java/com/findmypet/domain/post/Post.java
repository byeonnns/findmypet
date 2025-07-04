package com.findmypet.domain.post;

import com.findmypet.domain.common.Pet;
import com.findmypet.domain.common.PetType;
import com.findmypet.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "posts")
@Entity
public class Post {

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

    private String description;

    @Enumerated(EnumType.STRING)
    private PostStatus status; // 미해결, 매칭중, 해결됨

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Pet pet;// 반려동물 정보

    public static Post createPost(User writer, PostType postType, String title, String location, String description,
                                  Pet pet) {
        return Post.builder()
                .writer(writer)
                .title(title)
                .postType(postType)
                .location(location)
                .description(description)
                .pet(pet)
                .createdAt(LocalDateTime.now())
                .status(PostStatus.UNRESOLVED)
                .build();
    }

    public void updatePost(String title, String location, String description, Pet pet) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.pet = pet;
        stampUpdated();
    }

    public void resolve() {
        checkResolved();
        this.status = PostStatus.RESOLVED;
        stampUpdated();
    }

    private void checkResolved() {
        if (this.status == PostStatus.RESOLVED) {
            throw new IllegalStateException("이미 해결된 게시글입니다.");
        }
    }

    private void stampUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
