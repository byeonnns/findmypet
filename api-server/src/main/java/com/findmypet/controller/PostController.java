package com.findmypet.controller;

import com.findmypet.common.exception.general.PermissionDeniedException;
import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.dto.request.post.CreatePostRequest;
import com.findmypet.dto.response.PostResponse;
import com.findmypet.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody CreatePostRequest request,
            HttpServletRequest servletRequest   // 세션ID 꺼낼 용도
    ) {
        Long writerId = (Long) servletRequest.getAttribute(SESSION_USER_ID);
        if (writerId == null) {
            throw new PermissionDeniedException("로그인이 필요합니다.");
        }
        Long postId = postService.createPost(request, writerId);
        return ResponseEntity.ok(postId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(@RequestParam(value = "type", required = false) PostType type, @RequestParam(value = "status", required = false) PostStatus status) {
        return ResponseEntity.ok(postService.getPosts(type, status));
    }

    /*
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestPart("request") UpdatePostRequest request, @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments, HttpServletRequest requestContext) throws IOException {
        Long userId = (Long) requestContext.getAttribute(SESSION_USER_ID);
        postService.updatePost(id, request, attachments);
        return ResponseEntity.ok(postService.getPostById(id));
    }
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpServletRequest requestContext) {
        Long userId = (Long) requestContext.getAttribute(SESSION_USER_ID);
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolvePost(@PathVariable Long id, HttpServletRequest requestContext) {
        Long userId = (Long) requestContext.getAttribute(SESSION_USER_ID);
        postService.resolvePost(id);
        return ResponseEntity.ok().build();
    }
}
