package com.example.springbootredditclone.service;

import com.example.springbootredditclone.dto.CommentsDto;
import com.example.springbootredditclone.exceptions.PostNotFoundException;
import com.example.springbootredditclone.exceptions.SpringRedditException;
import com.example.springbootredditclone.mapper.CommentMapper;
import com.example.springbootredditclone.model.Comment;
import com.example.springbootredditclone.model.NotificationEmail;
import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.User;
import com.example.springbootredditclone.repository.CommentRepository;
import com.example.springbootredditclone.repository.PostRepository;
import com.example.springbootredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private static final String POST_URL = "";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void save(CommentsDto commentsDto) throws SpringRedditException {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post was not found. Id: -  " + commentsDto.getPostId().toString()));

        //we map objects from dto to our entity classes automatically
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        //we build an email to notify the user
        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post."+ POST_URL);

        //send the email
        sendCommentNotification(message, post.getUser());

    }

    private void sendCommentNotification(String message, User user) throws SpringRedditException {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }


    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post was not found with id: " + postId.toString()));

        return commentRepository.findByPost(post).stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());

    }

    public List<CommentsDto> getAllCommentsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
