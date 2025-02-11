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
        Post post = Post.builder()
                .userId(userId)
                .categoryId(postDto.getCategoryId())
                .platformId(postDto.getPlatformId())
                .languageId(postDto.getLanguageId())
                .postTitle(postDto.getPostTitle())
                .postContent(postDto.getPostContent())
                .build();
        postMapper.insertPost(post);
    }

    public void updatePost(Long userId, Long postId, PostRequestDto postDto){
        // 게시글 조회
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }
        // 게시글 작성자 ID 조회
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if (postOwnerId == null || !postOwnerId.equals(userId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        // 변경된 필드만 반영
        post = post.toBuilder()
                .platformId(postDto.getPlatformId() != null ? postDto.getPlatformId() : post.getPlatformId())
                .categoryId(postDto.getCategoryId() != null ? postDto.getCategoryId() : post.getCategoryId())
                .languageId(postDto.getLanguageId() != null ? postDto.getLanguageId() : post.getLanguageId())
                .postTitle(postDto.getPostTitle() != null ? postDto.getPostTitle() : post.getPostTitle())
                .postContent(postDto.getPostContent() != null ? postDto.getPostContent() : post.getPostContent())
                .build();
        postMapper.updatePost(post);
    }

    public void deletePost(Long userId, Long postId){
        // 게시글 조회
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("게시글이 존재하지 않습니다.");
        }
        // 게시글 작성자 ID 조회
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if (postOwnerId == null || !postOwnerId.equals(userId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        postMapper.deletePost(postId);
    }

    public List<PostResponseDto> getPostList() {
        return postMapper.selectPostList();
    }

    public PostResponseDto getPost(Long postId){
        return postMapper.selectPost(postId);
    }

}
