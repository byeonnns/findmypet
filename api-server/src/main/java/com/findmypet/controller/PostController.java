package com.findmypet.controller;

import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import com.findmypet.dto.request.CreatePostRequest;
import com.findmypet.dto.request.UpdatePostRequest;
import com.findmypet.dto.response.PostResponse;
import com.findmypet.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        Long postId = postService.createPost(request);
        URI location = URI.create("/api/posts/" + postId);
        PostResponse response = postService.getPostById(postId);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(value = "type", required = false) PostType type,
            @RequestParam(value = "status", required = false) PostStatus status
    ) {
        return ResponseEntity.ok(postService.getPosts(type, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestBody UpdatePostRequest request
    ) {
        postService.updatePost(id, request);
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Void> resolvePost(@PathVariable Long id) {
        postService.resolvePost(id);
        return ResponseEntity.ok().build();
    }
}