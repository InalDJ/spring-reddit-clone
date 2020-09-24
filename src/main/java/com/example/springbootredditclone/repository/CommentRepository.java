package com.example.springbootredditclone.repository;

import com.example.springbootredditclone.model.Comment;
import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
