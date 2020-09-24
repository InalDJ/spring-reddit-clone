package com.example.springbootredditclone.repository;

import com.example.springbootredditclone.model.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubredditRepository extends JpaRepository<Subreddit, Long> {


    Optional<Subreddit> findByName(String subredditName);
}
