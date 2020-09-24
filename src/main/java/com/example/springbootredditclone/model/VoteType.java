package com.example.springbootredditclone.model;

import com.example.springbootredditclone.exceptions.SpringRedditException;
import lombok.Data;

import java.util.Arrays;


public enum VoteType {
    UPVOTE(1), DOWNVOTE(-1),
    ;

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private int direction;

    VoteType(int direction) {
    }

    public static VoteType lookup(Integer direction) throws SpringRedditException {
        return Arrays.stream(VoteType.values())
                .filter(value -> value.getDirection().equals(direction))
                .findAny()
                .orElseThrow(() -> new SpringRedditException("Vote not found"));
    }
}
