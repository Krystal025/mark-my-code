package com.markmycode.mmc.post.repository;

import com.markmycode.mmc.post.dto.PostListRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Integer selectParentIdByCategoryId(Integer childCategoryId);
    PostResponseDto selectPost(Long postId);
    List<PostSummaryDto> selectPostsByFilters(PostListRequestDto postListRequestDto);
    List<PostSummaryDto> selectRecentPosts();
    List<PostSummaryDto> selectPopularPosts();
}


