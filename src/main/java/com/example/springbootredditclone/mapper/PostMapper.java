package com.example.springbootredditclone.mapper;

import com.example.springbootredditclone.dto.PostRequest;
import com.example.springbootredditclone.dto.PostResponse;
import com.example.springbootredditclone.model.*;
import com.example.springbootredditclone.repository.CommentRepository;
import com.example.springbootredditclone.repository.VoteRepository;
import com.example.springbootredditclone.service.AuthService;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.example.springbootredditclone.model.VoteType.DOWNVOTE;
import static com.example.springbootredditclone.model.VoteType.UPVOTE;

@Mapper(componentModel = "spring")
//changed from interface to n abstract class
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "user", source = "user")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);


    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")//method below
    public abstract PostResponse mapToDto(Post post);

    String getDuration(Post post){
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());//toEpochMilli() converts instant to a long type
    }

    Integer commentCount(Post post){
        return commentRepository.findByPost(post).size();
    }

    boolean isPostUpVoted(Post post){
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    private boolean checkVoteType(Post post, VoteType voteType){
        if(authService.isLoggedIn()){
            Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType))
                    .isPresent();
        }
        return false;
    }



}
