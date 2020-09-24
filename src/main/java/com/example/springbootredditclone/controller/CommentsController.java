package com.example.springbootredditclone.controller;

import com.example.springbootredditclone.dto.CommentsDto;
import com.example.springbootredditclone.exceptions.SpringRedditException;
import com.example.springbootredditclone.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) throws SpringRedditException {
        commentService.save(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("by-post/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId));
    }

    @GetMapping("by-user/{username}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username){
       return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(username));

    }
}