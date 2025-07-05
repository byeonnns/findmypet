package com.findmypet.service;

import com.findmypet.domain.common.Attachment;
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
                    log.error("[게시글 등록 요청 오류] 사용자를 찾을 수 없습니다. id = {}", request.getWriterId());
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
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

        if (request.getAttachmentUrls() != null && !request.getAttachmentUrls().isEmpty()) {
            List<Attachment> attachments = IntStream.range(0, request.getAttachmentUrls().size())
                    .mapToObj(i -> Attachment.builder()
                            .post(saved)
                            .url(request.getAttachmentUrls().get(i))
                            .sortOrder(i)
                            .build()
                    )
                    .collect(Collectors.toList());
            attachmentRepository.saveAll(attachments);

            log.info("[첨부파일 저장] postId = {}, {}개의 첨부파일", saved.getId(), request.getAttachmentUrls().size());
        }
        return saved.getId();
    }

    @Transactional
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    log.error("[단건 게시글 조회 요청 오류] 게시글을 찾을 수 없습니다. postId = {}", postId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
                });
        List<Attachment> attachments = attachmentRepository.findByPostId(postId);
        return PostResponse.from(post, attachments);
    }

    @Transactional
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
                    List<Attachment> attachments = attachmentRepository.findByPostId(p.getId());
                    return PostResponse.from(p, attachments);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    log.error("[게시글 수정 요청 오류] 게시글을 찾을 수 없습니다. postId = {}", postId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
                });

        Pet pet = Pet.builder()
                .species(request.getPetSpecies())
                .breed(request.getPetBreed())
                .age(request.getPetAge())
                .gender(request.getPetGender())
                .color(request.getPetColor())
                .build();

        post.updatePost(
                request.getTitle(),
                request.getLocation(),
                request.getDescription(),
                pet
        );
        postRepository.save(post);

        log.info("[게시글 수정] id = {}", postId);

        if (request.getAttachmentUrls() != null) {
            attachmentRepository.deleteByPostId(postId);
            List<Attachment> attachments = IntStream.range(0, request.getAttachmentUrls().size())
                    .mapToObj(i -> Attachment.builder()
                            .post(post)
                            .url(request.getAttachmentUrls().get(i))
                            .sortOrder(i)
                            .build()
                    )
                    .collect(Collectors.toList());
            attachmentRepository.saveAll(attachments);

            log.info("[첨부파일 수정] postId = {}, count = {}", postId, request.getAttachmentUrls().size());
        }
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    log.error("[게시글 삭제 요청 오류] 게시글을 찾을 수 없습니다. postId = {}", postId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
                });
        post.delete();
        postRepository.save(post);

        attachmentRepository.deleteByPostId(postId);

        log.info("[게시글 삭제] postId = {}", postId);
    }

    @Transactional
    public void resolvePost(Long postId) {
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> {
                    log.error("[게시글 해결 처리 요청 오류] 게시글을 찾을 수 없습니다. id = {}", postId);
                    return new IllegalArgumentException("게시글을 찾을 수 없습니다.");
                });
        post.resolve();
        postRepository.save(post);

        log.info("[게시글 해결 처리] postId = {}", postId);
    }
}