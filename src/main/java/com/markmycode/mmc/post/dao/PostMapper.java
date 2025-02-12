package com.markmycode.mmc.post.dao;

import com.markmycode.mmc.post.entity.Post;

import java.util.List;

public interface PostMapper {
    void insertPost(Post post);
    void updatePost(Post post);
    void deletePost(Long postId);
    List<Post> selectPostList();
    // PostResponseDto selectPost(Long postId);
    Post selectPostById(Long postId);
    Long selectUserIdByPostId(Long postId);
    String selectUserNicknameByPostId(Long postId);
    String selectPlatformNameByPostId(Long postId);
    String selectCategoryNameByPostId(Long postId);
    String selectLanguageNameByPostId(Long postId);

}
