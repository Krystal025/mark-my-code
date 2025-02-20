package com.markmycode.mmc.post.service;

import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dao.PostMapper;
import com.markmycode.mmc.post.dto.PostFilterRequestDto;
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
    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final LanguageService languageService;

    public void createPost(Long userId, PostRequestDto requestDto){
        Integer parentCategoryId = postMapper.selectParentCategoryIdByChildCategoryId(requestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, requestDto.getChildCategoryId());
        Post post = Post.builder()
                .userId(userId)
                .categoryId(requestDto.getChildCategoryId())
                .platformId(requestDto.getPlatformId())
                .languageId(requestDto.getLanguageId())
                .postTitle(requestDto.getPostTitle())
                .postContent(requestDto.getPostContent())
                .build();
        postMapper.insertPost(post);
    }

    public void updatePost(Long userId, Long postId, PostRequestDto requestDto){
        // 게시글 조회
        Post post = postMapper.selectPostById(postId);
        if (post == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(userId, postId);
        // 변경된 필드만 반영
        post = post.toBuilder()
                .categoryId(requestDto.getChildCategoryId() != null ? requestDto.getChildCategoryId() : post.getCategoryId())
                .platformId(requestDto.getPlatformId() != null ? requestDto.getPlatformId() : post.getPlatformId())
                .languageId(requestDto.getLanguageId() != null ? requestDto.getLanguageId() : post.getLanguageId())
                .postTitle(requestDto.getPostTitle() != null ? requestDto.getPostTitle() : post.getPostTitle())
                .postContent(requestDto.getPostContent() != null ? requestDto.getPostContent() : post.getPostContent())
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

    public PostResponseDto getPost(Long postId){
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
                .parentCategoryName(postMapper.selectParentCategoryNameByPostId(post.getPostId()))
                .childCategoryName(postMapper.selectChildCategoryNameByPostId(post.getPostId()))
                .languageName(postMapper.selectLanguageNameByPostId(post.getPostId()))
                .build();

    }

    public List<PostSummaryDto> getFilteredPosts(PostFilterRequestDto filterRequestDto){
        // 필터링 조건 유효성 검증 (JPA 사용)
        Integer parentCategoryId = postMapper.selectParentCategoryIdByChildCategoryId(filterRequestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, filterRequestDto.getChildCategoryId());
        platformService.validatePlatform(filterRequestDto.getPlatformId());
        languageService.validateLanguage(filterRequestDto.getLanguageId());
        // 필터링된 게시글 조회 (myBatis 사용)
        List<Post> posts = postMapper.selectPostsByFilters(filterRequestDto);
        if(posts.isEmpty()){
            throw new NotFoundException(ErrorCode.POSTS_NOT_FOUND);
        }
        return posts.stream()
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getPostId())
                        .postTitle(post.getPostTitle())
                        .postCreatedAt(post.getPostCreatedAt())
                        .postLike(post.getPostLike())
                        .userNickname(postMapper.selectUserNicknameByPostId(post.getPostId()))
                        .parentCategoryName(postMapper.selectParentCategoryNameByPostId(post.getPostId()))
                        .childCategoryName(postMapper.selectChildCategoryNameByPostId(post.getPostId()))
                        .platformName(postMapper.selectPlatformNameByPostId(post.getPostId()))
                        .languageName(postMapper.selectLanguageNameByPostId(post.getPostId()))
                        .build())
                .collect(Collectors.toList());
    }

    private void validatePostOwnership(Long userId, Long postId){
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if(postOwnerId == null || !postOwnerId.equals(userId)){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

}
