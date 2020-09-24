package com.example.springbootredditclone.mapper;

import com.example.springbootredditclone.dto.CommentsDto;
import com.example.springbootredditclone.model.Comment;
import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")//Why we write just java.time.instant.now instead of just instant.now. Because we need to add import. without it Instant would't be imported
    @Mapping(target = "post", source = "post")
    Comment map(CommentsDto commentsDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUsername())")
    CommentsDto mapToDto(Comment comment);
}
