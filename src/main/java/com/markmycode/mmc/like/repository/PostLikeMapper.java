package com.markmycode.mmc.like.repository;

import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.user.dto.UserSummaryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostLikeMapper {
    List<PostSummaryDto> selectLikedPostsByUser(Long userId);
    List<UserSummaryDto> selectUsersWhoLikedPost(Long postId);
}
