package com.markmycode.mmc.post.dao;

import com.markmycode.mmc.post.dto.PostFilterRequestDto;
import com.markmycode.mmc.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    void insertPost(Post post);
    void updatePost(Post post);
    void deletePost(Long postId);
    Post selectPostById(Long postId);
    List<Post> selectPostsByFilters(PostFilterRequestDto postFilterRequestDto);

    Long selectUserIdByPostId(Long postId);
    String selectUserNicknameByPostId(Long postId);
    String selectParentCategoryNameByPostId(Long postId);
    String selectChildCategoryNameByPostId(Long postId);
    String selectPlatformNameByPostId(Long postId);
    String selectLanguageNameByPostId(Long postId);
    Integer selectParentCategoryIdByChildCategoryId(Integer childCategoryId);
}

