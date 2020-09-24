package com.example.springbootredditclone.service;

import com.example.springbootredditclone.dto.AuthenticationResponse;
import com.example.springbootredditclone.dto.LoginRequest;
import com.example.springbootredditclone.dto.RefreshTokenRequest;
import com.example.springbootredditclone.dto.RegisterRequest;
import com.example.springbootredditclone.exceptions.SpringRedditException;
import com.example.springbootredditclone.model.NotificationEmail;
import com.example.springbootredditclone.model.User;
import com.example.springbootredditclone.model.VerificationToken;
import com.example.springbootredditclone.repository.UserRepository;
import com.example.springbootredditclone.repository.VerificationTokenRepository;
import com.example.springbootredditclone.security.JwtProvider;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    //we need to create a bean in SecurityConfig
    //there are many implementations of this interface
    //if we don't create a bean in SecurityConfig we get an exception
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;



    //get data from our class to pass login/password
    @Transactional//because we work with a relational db
    public void signup(RegisterRequest registerRequest) throws SpringRedditException {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        //we encode a password
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());

        //before email confirmation, a user is not valid
        //after validation he becomes an accredited user
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        //now we send mailTemplate html code as an email to the user
        //to confirm the registration
        mailService.sendMail(new NotificationEmail("Please Activate your Account", user.getEmail(), "Thank you for signing up" +
                "to Spring Reddit, " +
                "please click on the below url to activate your account: " +
                "http://localhost:8080/api/auth/accountVerification/"+token));
    }

    private String generateVerificationToken(User user) {
        //we generate a random token to send it via email to activate a user
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }


    public void verifyAccount(String token) throws SpringRedditException {
        //We check if the token exists in the db
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token).orElseThrow(()-> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken);
    }

    //We find the user and make him Enabled to grant him access
    @Transactional//Because we save the user in the db
    void fetchUserAndEnable(VerificationToken verificationToken) throws SpringRedditException {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name: - " + username));
        //make him enabled
        user.setEnabled(true);
        //save the user
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) throws SpringRedditException {
        //we check ifuser is auth or not
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        //we generate a token here
        String token = jwtProvider.generateToken(authenticate);

        //now we need to send a token to the user
        //we create a class dto.AuthenticationResponse and create an object to pass it to Angular via REST
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }

    @Transactional
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - "+ principal.getUsername()));
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();

    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        //now we need to generate a new token
        //we cannot use the old generateToken method from JwtProvider
        //because we don't have user's name here
        //so we create one more method already with username
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
}
