package com.example.springbootredditclone.repository;

import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.Subreddit;
import com.example.springbootredditclone.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
