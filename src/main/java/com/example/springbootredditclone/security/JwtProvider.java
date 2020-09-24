package com.example.springbootredditclone.security;

import com.example.springbootredditclone.exceptions.SpringRedditException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtProvider {


    private KeyStore keyStore;

    //add a property to application.properties
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;


    //now we generate a real jwt token after adding dependencies
    public String generateToken(Authentication authentication) throws SpringRedditException {
        //cast to import org.springframework.security.core.userdetails.User; not to Entity User
        User principal = (User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUserName(String username) throws SpringRedditException {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    //we create a key and convert it to char array
    private PrivateKey getPrivateKey() throws SpringRedditException {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw new SpringRedditException("Exception occured while retrieving public key from keystore");
        }
    }

    //it's better to handle multiple exceptions
    @PostConstruct//to open it at the beginning of the loading
    public void init() throws SpringRedditException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        try {
            keyStore = KeyStore.getInstance("JKS");
            //we need to create this file near application.properties or it won't work
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore");
        }
    }

    public boolean validateToken(String jwt) throws SpringRedditException {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() throws SpringRedditException {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("AN Error occurred while retrieving public key");
        }

    }

    public String getUsernameFromJwt(String token) throws SpringRedditException {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis;
    }
}

    /*
    * private String secret = "javajavajavajavajavajavajavajavajavajavajava";

    //now we generate a real jwt token after adding dependencies
    public String generateToken(Authentication authentication) throws SpringRedditException {
        //cast to import org.springframework.security.core.userdetails.User; not to Entity User
        User principal = (User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }



    public boolean validateToken(String jwt) throws SpringRedditException {
        Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt);
        return true;
    }


    public String getUsernameFromJwt(String token) throws SpringRedditException {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }*/







/*
 *
    private KeyStore keyStore;


    //now we generate a real jwt token after adding dependencies
    public String generateToken(Authentication authentication) throws SpringRedditException {
        //cast to import org.springframework.security.core.userdetails.User; not to Entity User
         User principal = (User)authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    //we create a key and convert it to char array
    private PrivateKey getPrivateKey() throws SpringRedditException {
        try {
            return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw new SpringRedditException("Exception occured while retrieving public key from keystore", new Exception());
        }
    }

    //it's better to handle multiple exceptions
    @PostConstruct//to open it at the beginning of the loading
    public void init() throws SpringRedditException, KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        try {
            keyStore = KeyStore.getInstance("JKS");
            //we need to create this file near application.properties or it won't work
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "secret".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception occurred while loading keystore", new Exception());
        }
    }

    public boolean validateToken(String jwt) throws SpringRedditException {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() throws SpringRedditException {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("AN Error occurred while retrieving public key", new Exception());
        }

    }

    public String getUsernameFromJwt(String token) throws SpringRedditException {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

 * */
