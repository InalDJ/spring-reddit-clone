package com.example.springbootredditclone;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
//imported swagger
//and also we need to add it to our security class to grant access
@Import(SwaggerConfiguration.class)
@SpringBootApplication
@EnableAsync
public class SpringBootRedditCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedditCloneApplication.class, args);
    }

}
