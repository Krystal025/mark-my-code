package com.markmycode.mmc.post.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.post.dao.PostMapper;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(userId, postId);
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
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(userId, postId);
        postMapper.deletePost(postId);
    }

    public List<PostSummaryDto> getPostList() {
        List<Post> postList = postMapper.selectPostList();
        if(postList.isEmpty()){
            throw new NotFoundException(ErrorCode.POSTS_NOT_FOUND);
        }
        return postList.stream()
                .map(post -> PostSummaryDto.builder() // post : postList에 저장된 각 Post 객체
                        .postId(post.getPostId())
                        .postTitle(post.getPostTitle())
                        .postCreatedAt(post.getPostCreatedAt())
                        .postLike(post.getPostLike())
                        .userNickname(postMapper.selectUserNicknameByPostId(post.getPostId())) // post에서 userId를 가져와서 사용
                        .build())
                .collect(Collectors.toList());
    }

    public PostResponseDto getPost(Long postId){
        // 게시글 조회
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .postCreatedAt(post.getPostCreatedAt())
                .postUpdatedAt(post.getPostUpdatedAt())
                .postLike(post.getPostLike())
                .userNickname(postMapper.selectUserNicknameByPostId(post.getPostId()))
                .platformName(postMapper.selectPlatformNameByPostId(post.getPostId()))
                .categoryName(postMapper.selectCategoryNameByPostId(post.getPostId()))
                .languageName(postMapper.selectLanguageNameByPostId(post.getPostId()))
                .build();

    }

    private void validatePostOwnership(Long userId, Long postId){
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if(postOwnerId == null || !postOwnerId.equals(userId)){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }
}
