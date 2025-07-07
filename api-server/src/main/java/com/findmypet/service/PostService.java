package com.findmypet.service;

import com.findmypet.common.exception.ResourceNotFoundException;
import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import com.findmypet.domain.common.Pet;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.CreatePostRequest;
import com.findmypet.dto.request.UpdatePostRequest;
import com.findmypet.dto.response.PostResponse;
import com.findmypet.repository.AttachmentRepository;
import com.findmypet.repository.PostRepository;
import com.findmypet.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       AttachmentRepository attachmentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional
    public Long createPost(CreatePostRequest request) {
        User writer = userRepository.findById(request.getWriterId())
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });

        Pet pet = Pet.builder()
                .species(request.getPetSpecies())
                .breed(request.getPetBreed())
                .age(request.getPetAge())
                .gender(request.getPetGender())
                .color(request.getPetColor())
                .build();

        Post post = Post.createPost(
                writer,
                request.getPostType(),
                request.getTitle(),
                request.getLocation(),
                request.getDescription(),
                pet
        );

        Post saved = postRepository.save(post);

        log.info("[게시글 생성] postId = {}, writerId= {} , title= {} ", post.getId(), request.getWriterId(), request.getTitle());

        List<String> urls = request.getAttachmentUrls();
        if (urls != null && !urls.isEmpty()) {
            List<Attachment> attachments = IntStream.range(0, urls.size())
                    .mapToObj(i -> Attachment.builder()
                            .url(urls.get(i))
                            .sortOrder(i)
                            .attachmentType(AttachmentType.POST)
                            .targetId(saved.getId())
                            .build()
                    )
                    .collect(Collectors.toList());
            attachmentRepository.saveAll(attachments);

            log.info("[첨부파일 저장] postId = {} count = {}", saved.getId(), attachments.size());
        }

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
                });

        List<Attachment> attachments = attachmentRepository.findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType.POST, postId);

        return PostResponse.from(post, attachments);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPosts(PostType type, PostStatus status) {
        List<Post> posts;
        if (type == null && status == null) {
            posts = postRepository.findAllNotDeleted();
        } else if (type != null && status == null) {
            posts = postRepository.findByPostTypeAndNotDeleted(type);
        } else if (type == null) {
            posts = postRepository.findByStatusAndNotDeleted(status);
        } else {
            posts = postRepository.findByPostTypeAndStatusAndNotDeleted(type, status);
        }
        return posts.stream()
                .map(p -> {
                    List<Attachment> attachments = attachmentRepository.findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(
                            AttachmentType.POST, p.getId()
                    );
                    return PostResponse.from(p, attachments);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. postId=" + postId));

        Pet pet = Pet.builder()
                .species(request.getPetSpecies())
                .breed(request.getPetBreed())
                .age(request.getPetAge())
                .gender(request.getPetGender())
                .color(request.getPetColor())
                .build();
        post.updatePost(request.getTitle(), request.getLocation(), request.getDescription(), pet);
        postRepository.save(post);

        log.info("[게시글 수정] postId = {}", postId);

        // 2) 첨부파일 변경 로직
        List<String> newUrls = request.getAttachmentUrls();
        if (newUrls != null) {
            // 기존 첨부 조회
            List<Attachment> existing = attachmentRepository
                    .findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType.POST, postId);

            // 삭제해야 할 첨부 (요청에 없는 URL)
            List<Attachment> toRemove = existing.stream()
                    .filter(a -> !newUrls.contains(a.getUrl()))
                    .collect(Collectors.toList());
            if (!toRemove.isEmpty()) {
                attachmentRepository.deleteAll(toRemove);
                log.info("[첨부파일 삭제] postId={} removedCount={}", postId, toRemove.size());
            }

            // 새로 추가할 첨부 (기존에 없던 URL)
            List<Attachment> toAdd = IntStream.range(0, newUrls.size())
                    .filter(i -> existing.stream().noneMatch(a -> a.getUrl().equals(newUrls.get(i))))
                    .mapToObj(i -> Attachment.builder()
                            .url(newUrls.get(i))
                            .sortOrder(i)
                            .attachmentType(AttachmentType.POST)
                            .targetId(postId)
                            .build())
                    .collect(Collectors.toList());
            if (!toAdd.isEmpty()) {
                attachmentRepository.saveAll(toAdd);
                log.info("[첨부파일 추가] postId={} addedCount={}", postId, toAdd.size());
            }

            // 순서만 바뀐 첨부에 대해 sortOrder만 업데이트
            List<Attachment> toReorder = existing.stream()
                    .filter(a -> {
                        int idx = newUrls.indexOf(a.getUrl());
                        return idx >= 0 && a.getSortOrder() != idx;
                    })
                    .peek(a -> a.updateSortOrder(newUrls.indexOf(a.getUrl())))
                    .collect(Collectors.toList());
            if (!toReorder.isEmpty()) {
                attachmentRepository.saveAll(toReorder);
                log.info("[첨부파일 순서 변경] postId = {} updatedCount = {}", postId, toReorder.size());
            }
        }
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
                });
        post.delete();
        postRepository.save(post);

        attachmentRepository.deleteByAttachmentTypeAndTargetId(AttachmentType.POST, postId);

        log.info("[게시글 삭제] postId = {}", postId);
    }

    @Transactional
    public void resolvePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("게시글을 찾을 수 없습니다.");
                });
        post.resolve();
        postRepository.save(post);

        log.info("[게시글 해결 처리] postId = {}", postId);
    }
}