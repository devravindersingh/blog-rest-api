package com.ravinder.project.blog.service.impl;

import com.ravinder.project.blog.entity.Comment;
import com.ravinder.project.blog.entity.Post;
import com.ravinder.project.blog.exception.BlogAPIException;
import com.ravinder.project.blog.exception.ResourceNotFoundException;
import com.ravinder.project.blog.payload.CommentDto;
import com.ravinder.project.blog.repository.CommentRepository;
import com.ravinder.project.blog.repository.PostRepository;
import com.ravinder.project.blog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper mapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
//        retrieve post entity by id
        Post post = postRepository
                .findById(postId).orElseThrow(()-> new ResourceNotFoundException("Post", "Id", postId));
//        set post to comment entity - join table will be created with
        comment.setPost(post);
//        save comment to db and return commentDto
        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
//        retrieve comments by postId
        List<Comment> commentList = commentRepository.findByPostId(postId);
//        comment list coverted to commentDto list
        List<CommentDto> commentDtoList = commentList
                .stream()
                .map(comment -> mapToDto(comment))
                .collect(Collectors.toList());
        return commentDtoList;
    }

    @Override
    public CommentDto getCommentByPostId(Long postId, Long commentId) {
//        retrieve post entity by id
        Post post = postRepository
                .findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
//        retrieve comment by commentId from postId
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment", "id", commentId));
//        check if comment's post id is equal to post id
        if (!comment.getPost().getId().equals(postId))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");

        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
//        retrieve post entity by id
        Post post = postRepository
                .findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
//        retrieve comment by commentId from postId
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment", "id", commentId));
//        check if comment's post id is equal to post id
        if (!comment.getPost().getId().equals(postId))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");

        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        //save to db and return as dto
        return mapToDto(commentRepository.save(comment));
    }

    @Override
    public String deleteComment(Long postId, Long commentId) {
//        retrieve post entity by id
        Post post = postRepository
                .findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
//        retrieve comment by commentId from postId
        Comment comment = commentRepository
                .findById(commentId).orElseThrow(()-> new ResourceNotFoundException("Comment", "id", commentId));
//        check if comment's post id is equal to post id
        if (!comment.getPost().getId().equals(postId))
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");

        commentRepository.delete(comment);
        return "Comment deleted";

    }

    private CommentDto mapToDto(Comment comment){
        CommentDto commentDto = mapper.map(comment, CommentDto.class);
//        CommentDto commentDto = new CommentDto(
//                comment.getId(),
//                comment.getName(),
//                comment.getEmail(),
//                comment.getBody()
//        );
        return commentDto;
    }

    private Comment mapToEntity(CommentDto commentDto){
        Comment comment = mapper.map(commentDto, Comment.class);
//        Comment comment = new Comment();
//        comment.setName(commentDto.getName());
//        comment.setEmail(commentDto.getEmail());
//        comment.setBody(commentDto.getBody());
        return comment;
    }


}
