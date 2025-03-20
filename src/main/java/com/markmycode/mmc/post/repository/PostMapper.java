package com.markmycode.mmc.post.repository;

import com.markmycode.mmc.post.dto.PostListRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostPreviewResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Integer selectParentIdByCategoryId(Integer childCategoryId);
    PostResponseDto selectPost(Long postId);
    List<PostPreviewResponseDto> selectPostsByFilters(PostListRequestDto postListRequestDto);
    long countPostsByFilters(PostListRequestDto requestDto);
    List<PostPreviewResponseDto> selectRecentPosts();
    List<PostPreviewResponseDto> selectPopularPosts();
}


