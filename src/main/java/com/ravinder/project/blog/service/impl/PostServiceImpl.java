package com.ravinder.project.blog.service.impl;

import com.ravinder.project.blog.entity.Category;
import com.ravinder.project.blog.entity.Post;
import com.ravinder.project.blog.entity.PostResponse;
import com.ravinder.project.blog.exception.ResourceNotFoundException;
import com.ravinder.project.blog.payload.PostDto;
import com.ravinder.project.blog.repository.CategoryRepository;
import com.ravinder.project.blog.repository.PostRepository;
import com.ravinder.project.blog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;
    private ModelMapper mapper;

    public PostServiceImpl(PostRepository postRepository, CategoryRepository categoryRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));
        //convert Dto to entity
        Post post = mapToEntity(postDto);
        post.setCategory(category);
        //save to DB
        Post savedPost = postRepository.save(post);
        //convert entity to Dto
        PostDto postResponse = mapToDto(savedPost);
        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {

        //create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        Page<Post> postPage = postRepository.findAll(pageable);

        List<Post> postList = postPage.getContent();

        List<PostDto> postDtoList = postList.stream().map(post -> mapToDto(post)).collect(Collectors.toList());
        PostResponse postResponse = PostResponse.builder()
                .content(postDtoList)
                .pageNo(postPage.getNumber())
                .pageSize(postPage.getSize())
                .totalElements(postPage.getTotalElements())
                .totalPages(postPage.getTotalPages())
                .isLast(postPage.isLast()).build();

        return postResponse;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", id));
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long postId) {
        //get post By id from db
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));

        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());
        post.setCategory(category);
        //above update saved
        Post updatedPost = postRepository.save(post);

        return mapToDto(updatedPost);
    }

    @Override
    public String deletePostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "Id", postId));
        postRepository.deleteById(postId);
        return "Resource Deleted Successfully";
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return postRepository.findByCategoryId(categoryId)
                .stream()
                .map(post -> mapToDto(post)).collect(Collectors.toList());
    }


    //convert entity to Dto
    private PostDto mapToDto(Post post){

        PostDto postDto = mapper.map(post, PostDto.class);
        return postDto;
    }
    //convert Dto to entity
    private Post mapToEntity(PostDto postDto){

        Post post = mapper.map(postDto, Post.class);
        return post;
    }

}
