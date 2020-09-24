package com.example.springbootredditclone.repository;

import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.User;
import com.example.springbootredditclone.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    //user will see his votes from the latest to newest in descending order
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
