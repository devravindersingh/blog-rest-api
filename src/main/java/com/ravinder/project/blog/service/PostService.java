package com.ravinder.project.blog.service;

import com.ravinder.project.blog.entity.PostResponse;
import com.ravinder.project.blog.payload.PostDto;

public interface PostService {
    PostDto createPost(PostDto postDto);
    PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);
    PostDto getPostById(Long id);
    PostDto updatePost(PostDto postDto, Long postId);
    String deletePostById(Long postId);
}
