//1//
package com.example.springredditclone.mapper;

import com.example.springredditclone.dto.SubredditDto;
import com.example.springredditclone.model.Post;
import com.example.springredditclone.model.Subreddit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {
    //dto takes subreddit as input, numberOfPosts will returns size of posts
    //annotation Mapping to tell to mapstruct when mapping the post field
    @Mapping(target = "numberOfPosts", expression = "java(mapPosts(subreddit.getPosts()))")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

    //which takes in a list of post as input
    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    //annotation to create mapping
    @InheritInverseConfiguration
    //ignore post field because we set the post field when creating the post
    @Mapping(target = "posts", ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto);
    //and this time to refactor existing code by removing mapping method, back to subredditService
}
