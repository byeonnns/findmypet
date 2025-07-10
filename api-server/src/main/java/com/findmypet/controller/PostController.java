package com.findmypet.controller;

import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.dto.request.CreatePostRequest;
import com.findmypet.dto.request.UpdatePostRequest;
import com.findmypet.dto.response.PostResponse;
import com.findmypet.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.findmypet.config.auth.SessionConst.SESSION_USER_ID;

@Slf4j
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Long> createPost(@RequestPart("request") CreatePostRequest request, @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) throws IOException {
        Long postId = postService.createPost(request, attachments);
        return ResponseEntity.ok(postId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(value = "type", required = false) PostType type,
            @RequestParam(value = "status", required = false) PostStatus status) {
        return ResponseEntity.ok(postService.getPosts(type, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody UpdatePostRequest request, HttpServletRequest requestContext) {
        Long userId = (Long) requestContext.getAttribute(SESSION_USER_ID);
        postService.updatePost(id, request);
        return ResponseEntity.ok(postService.getPostById(id));
    }

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
