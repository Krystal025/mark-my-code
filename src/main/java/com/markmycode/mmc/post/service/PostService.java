package com.markmycode.mmc.post.service;

import com.markmycode.mmc.post.dao.PostMapper;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;

    public void createPost(Long userId, PostRequestDto postDto){
        Post newPost = Post.builder()
                .userId(userId)
                .categoryId(postDto.getCategoryId())
                .platformId(postDto.getPlatformId())
                .languageId(postDto.getLanguageId())
                .postTitle(postDto.getPostTitle())
                .postContent(postDto.getPostContent())
                .build();
        postMapper.insertPost(newPost);
    }

    public void updatePost(Long userId, Long postId, PostRequestDto postDto){
        // 1. 게시글 조회
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }
        // 2. 게시글 소유자 ID 조회
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if (postOwnerId == null || !postOwnerId.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        Post updatedPost = Post.builder()
                .postId(postId)
                .categoryId(postDto.getCategoryId())
                .platformId(postDto.getPlatformId())
                .languageId(postDto.getLanguageId())
                .postTitle(postDto.getPostTitle())
                .postContent(postDto.getPostContent())
                .build();
        postMapper.updatePost(updatedPost);
    }

    public void deletePost(Long postId){
        postMapper.deletePost(postId);
    }

    public List<PostResponseDto> getPostList() {
        return postMapper.selectPostList();
    }

    public PostResponseDto getPost(Long postId){
        return postMapper.selectPost(postId);
    }

}
