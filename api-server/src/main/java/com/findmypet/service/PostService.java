package com.findmypet.service;

import com.findmypet.common.exception.general.ResourceNotFoundException;
import com.findmypet.domain.common.Attachment;
import com.findmypet.domain.common.AttachmentType;
import com.findmypet.domain.common.Pet;
import com.findmypet.domain.post.Post;
import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.domain.user.User;
import com.findmypet.dto.request.post.CreatePostRequest;
import com.findmypet.dto.response.PostResponse;
import com.findmypet.repository.AttachmentRepository;
import com.findmypet.repository.PostRepository;
import com.findmypet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public Long createPost(CreatePostRequest request, Long writerId) {
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        Pet pet = Pet.builder()
                .species(request.getPetSpecies())
                .breed(request.getPetBreed())
                .age(request.getPetAge())
                .gender(request.getPetGender())
                .color(request.getPetColor())
                .build();

        Post post = Post.create(
                writer,
                request.getPostType(),
                request.getTitle(),
                request.getLocation(),
                request.getDescription(),
                pet
        );
        Post saved = postRepository.save(post);

        log.info("[게시글 생성] postId={}, writerId={}, title={}", saved.getId(), writer.getId(), saved.getTitle());

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
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
                    List<Attachment> attachments = attachmentRepository.findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType.POST, p.getId());
                    return PostResponse.from(p, attachments);
                })
                .collect(Collectors.toList());
    }

    /*
    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request, List<MultipartFile> newFiles) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다. postId=" + postId));

        Pet pet = Pet.builder()
                .species(request.getPetSpecies())
                .breed(request.getPetBreed())
                .age(request.getPetAge())
                .gender(request.getPetGender())
                .color(request.getPetColor())
                .build();

        post.update(request.getTitle(), request.getLocation(), request.getDescription(), pet);
        postRepository.save(post);

        log.info("[게시글 수정] postId = {}", postId);

        List<String> urls = Optional.ofNullable(request.getAttachmentUrls()).orElse(List.of());

        // 기존 첨부 조회
        List<Attachment> existing = attachmentRepository.findByAttachmentTypeAndTargetIdOrderBySortOrderAsc(AttachmentType.POST, postId);

        // 삭제
        List<Attachment> toRemove = existing.stream()
                .filter(a -> !urls.contains(a.getUrl()))
                .peek(Attachment::markDeleted)
                .collect(Collectors.toList());
        if (!toRemove.isEmpty()) {
            attachmentRepository.saveAll(toRemove);
            log.info("[첨부파일 삭제] postId={} removedCount={}", postId, toRemove.size());
        }

        // 순서 변경
        List<Attachment> toReorder = existing.stream()
                .filter(a -> {
                    int idx = urls.indexOf(a.getUrl());
                    return idx >= 0 && a.getSortOrder() != idx;
                })
                .peek(a -> a.updateSortOrder(urls.indexOf(a.getUrl())))
                .collect(Collectors.toList());
        if (!toReorder.isEmpty()) {
            attachmentRepository.saveAll(toReorder);
            log.info("[첨부파일 순서 변경] postId = {} updatedCount = {}", postId, toReorder.size());
        }

        // 새 파일 업로드
        if (newFiles != null && !newFiles.isEmpty()) {
            for (int i = 0; i < newFiles.size(); i++) {
                MultipartFile file = newFiles.get(i);
                try {
                    int sortOrder = urls.size() + i;
                    Attachment atch = attachmentUploader.upload(file, sortOrder, AttachmentType.POST, postId, post.getWriter().getId());
                    log.info("[첨부파일 추가] postId = {}, attachmentId = {}", postId, atch.getId());
                } catch (RuntimeException e) {
                    log.warn("[첨부파일 추가 실패] postId={}, file={}, error={}", postId, file.getOriginalFilename(), e.getMessage());
                }
            }
        }
    }

     */

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        post.delete();
        postRepository.save(post);

        List<Attachment> attachments = attachmentRepository.findByAttachmentTypeAndTargetId(AttachmentType.POST, postId);
        attachments.forEach(Attachment::markDeleted);
        attachmentRepository.saveAll(attachments);

        log.info("[게시글 삭제] postId = {}", postId);
    }

    @Transactional
    public void resolvePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글을 찾을 수 없습니다."));
        post.resolve();
        postRepository.save(post);

        log.info("[게시글 해결 처리] postId = {}", postId);
    }
}
