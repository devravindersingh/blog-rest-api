package com.ravinder.project.blog.repository;

import com.ravinder.project.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
