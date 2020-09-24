package com.example.springbootredditclone.mapper;

import com.example.springbootredditclone.dto.SubredditDto;
import com.example.springbootredditclone.model.Post;
import com.example.springbootredditclone.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    //normally when we save an entitry from Angular we get is as DTO
    //THen we make like this object.setName(dto.getName())
    //to avoid this we use mapstruct
    //it creates a class SubredditMapperImpl where all the logic goes
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts){
        return numberOfPosts.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);

    //now we replace methods in Subredditservice with these methods
}
