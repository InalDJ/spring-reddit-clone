package com.example.springbootredditclone.service;

import com.example.springbootredditclone.dto.SubredditDto;
import com.example.springbootredditclone.exceptions.SpringRedditException;
import com.example.springbootredditclone.mapper.SubredditMapper;
import com.example.springbootredditclone.model.Subreddit;
import com.example.springbootredditclone.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
       return Subreddit.builder().name(subredditDto.getName())
                .description(subredditDto.getDescription())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll(){
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditDto)
                .collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) throws SpringRedditException {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with id: "+ id));
        return subredditMapper.mapSubredditDto(subreddit);
    }

}
