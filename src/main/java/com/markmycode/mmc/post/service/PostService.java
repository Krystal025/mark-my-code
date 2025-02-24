package com.markmycode.mmc.post.service;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.category.repository.CategoryRepository;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.language.entity.Language;
import com.markmycode.mmc.language.repository.LanguageRepository;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.entity.Platform;
import com.markmycode.mmc.platform.repository.PlatformRepository;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dto.PostFilterRequestDto;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.repository.PostMapper;
import com.markmycode.mmc.post.repository.PostRepository;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformRepository platformRepository;
    private final LanguageRepository languageRepository;

    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final LanguageService languageService;

    public void createPost(Long userId, PostRequestDto requestDto){
        Integer parentCategoryId = postMapper.selectParentIdByCategoryId(requestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, requestDto.getChildCategoryId());
        User user = getUser(userId);
        Category category = getCategory(requestDto.getChildCategoryId());
        Platform platform = getPlatform(requestDto.getPlatformId());
        Language language = getLanguage(requestDto.getLanguageId());
        Post post = Post.builder()
                .user(user)
                .category(category)
                .platform(platform)
                .language(language)
                .postTitle(requestDto.getPostTitle())
                .postContent(requestDto.getPostContent())
                .build();
        postRepository.save(post);
    }

    public void updatePost(Long userId, Long postId, PostRequestDto requestDto){
        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(userId, postId);
        Category category = requestDto.getChildCategoryId() != null ? getCategory(requestDto.getChildCategoryId()) : post.getCategory();
        Platform platform = requestDto.getPlatformId() != null ? getPlatform(requestDto.getPlatformId()) : post.getPlatform();
        Language language = requestDto.getLanguageId() != null ? getLanguage(requestDto.getLanguageId()) : post.getLanguage();
        // 변경된 필드만 반영
        post = post.toBuilder()
                .category(category)
                .platform(platform)
                .language(language)
                .postTitle(requestDto.getPostTitle() != null ? requestDto.getPostTitle() : post.getPostTitle())
                .postContent(requestDto.getPostContent() != null ? requestDto.getPostContent() : post.getPostContent())
                .build();
        postRepository.save(post);
    }

    public void deletePost(Long userId, Long postId){
        // 게시글 조회
        postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(userId, postId);
        postRepository.deleteById(postId);
    }

    public PostResponseDto getPost(Long postId){
        PostResponseDto responseDto = postMapper.selectPost(postId);
        if (responseDto == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return responseDto;
    }

    public List<PostSummaryDto> getFilteredPosts(PostFilterRequestDto filterRequestDto){
        validateFilterCondition(filterRequestDto);
        List<PostSummaryDto> posts = postMapper.selectPostsByFilters(filterRequestDto);
        if(posts.isEmpty()){
            throw new NotFoundException(ErrorCode.POSTS_NOT_FOUND);
        }
        return posts;
    }

    private void validatePostOwnership(Long userId, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        if(!post.getUser().getUserId().equals(userId)){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }
    // 필터링 조건 유효성 검증
    private void validateFilterCondition(PostFilterRequestDto filterRequestDto){
        Integer parentCategoryId = postMapper.selectParentIdByCategoryId(filterRequestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, filterRequestDto.getChildCategoryId());
        platformService.validatePlatform(filterRequestDto.getPlatformId());
        languageService.validateLanguage(filterRequestDto.getLanguageId());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    private Category getCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Platform getPlatform(Integer platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLATFORM_NOT_FOUND));
    }

    private Language getLanguage(Integer languageId) {
        return languageRepository.findById(languageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.LANGUAGE_NOT_FOUND));
    }


}
