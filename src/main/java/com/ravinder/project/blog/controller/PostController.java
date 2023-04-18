package com.ravinder.project.blog.controller;

import com.ravinder.project.blog.entity.PostResponse;
import com.ravinder.project.blog.payload.PostDto;
import com.ravinder.project.blog.service.PostService;
import com.ravinder.project.blog.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(
        name = "CRUD REST APIs for Post Resource"
)
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create Post")
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto){
        PostDto postDtoResponse = postService.createPost(postDto);
        return new ResponseEntity<>(postDtoResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all Posts")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping
    public ResponseEntity<PostResponse> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NO, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIR, required = false) String sortDir
    ){
        PostResponse postResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(postResponse);

    }

    @Operation(summary = "Get a Post")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(value = "id") Long postId){
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @Operation(summary = "Update a Post")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<PostDto> updatePostById(@Valid @RequestBody PostDto postDto, @PathVariable(value = "id") Long postId){
        PostDto postResponse = postService.updatePost(postDto, postId);
        return ResponseEntity.ok(postResponse);
    }

    @Operation(summary = "Delete a Post")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePostById(@PathVariable(value = "id") Long postId){
        return ResponseEntity.ok(postService.deletePostById(postId));
    }

    @Operation(summary = "Get all Posts by Category")
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 OK"
    )
    @GetMapping("/category/{id}")
    public ResponseEntity<List<PostDto>> getAllPostsByCategory(@PathVariable(value = "id") Long categoryId){
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }
}
