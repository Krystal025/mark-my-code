package com.markmycode.mmc.post.service;

import com.markmycode.mmc.category.repository.CategoryRepository;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.language.repository.LanguageRepository;
import com.markmycode.mmc.platform.repository.PlatformRepository;
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
    private final PlatformRepository platformRepository;
    private final CategoryRepository categoryRepository;
    private final LanguageRepository languageRepository;

    public void createPost(Long userId, PostRequestDto requestDto){
        Post post = Post.builder()
                .userId(userId)
                .categoryId(requestDto.getCategoryId())
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
                .platformId(requestDto.getPlatformId() != null ? requestDto.getPlatformId() : post.getPlatformId())
                .categoryId(requestDto.getCategoryId() != null ? requestDto.getCategoryId() : post.getCategoryId())
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

    public List<PostSummaryDto> getPosts() {
        List<Post> posts = postMapper.selectPosts();
        if(posts.isEmpty()){
            throw new NotFoundException(ErrorCode.POSTS_NOT_FOUND);
        }
        return posts.stream()
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

    public List<PostSummaryDto> getFilteredPosts(PostFilterRequestDto filterRequestDto){
        // 필터링 조건 유효성 검증 (JPA 사용)
        validateFilters(
                filterRequestDto.getPlatformId(),
                filterRequestDto.getCategoryId(),
                filterRequestDto.getLanguageId()
        );
        // 필터링된 게시글 조회 (myBatis 사용)
        List<Post> posts = postMapper.selectPostsByFilters(filterRequestDto);
        // int offset = (filterRequestDto.getPage() - 1) * filterRequestDto.getSize(); // 시작 위치 계산
        return posts.stream()
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getPostId())
                        .platformId(post.getPlatformId())
                        .categoryId(post.getCategoryId())
                        .languageId(post.getLanguageId())
                        .postTitle(post.getPostTitle())
                        .postCreatedAt(post.getPostCreatedAt())
                        .postLike(post.getPostLike())
                        .userNickname(postMapper.selectUserNicknameByPostId(post.getPostId()))
                        .build())
                .collect(Collectors.toList());
    }

    private void validatePostOwnership(Long userId, Long postId){
        Long postOwnerId = postMapper.selectUserIdByPostId(postId);
        if(postOwnerId == null || !postOwnerId.equals(userId)){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

    private void validateFilters(Integer platformId, Integer categoryId, Integer languageId){
        if (platformId != null && !platformRepository.existsById(platformId)) {
            throw new NotFoundException(ErrorCode.PLATFORM_NOT_FOUND);
        }
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        if (languageId != null && !languageRepository.existsById(languageId)) {
            throw new NotFoundException(ErrorCode.LANGUAGE_NOT_FOUND);
        }
    }

}
