package com.findmypet.repository;

import com.findmypet.domain.post.Post;
import com.findmypet.domain.post.PostStatus;
import com.findmypet.domain.post.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.isDeleted = false")
    Optional<Post> findByIdAndNotDeleted(Long id);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    List<Post> findAllNotDeleted();

    @Query("SELECT p FROM Post p WHERE p.postType = :type AND p.isDeleted = false")
    List<Post> findByPostTypeAndNotDeleted(PostType type);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.isDeleted = false")
    List<Post> findByStatusAndNotDeleted(PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.postType = :type AND p.status = :status AND p.isDeleted = false")
    List<Post> findByPostTypeAndStatusAndNotDeleted(PostType type, PostStatus status);
}