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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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

    @Transactional
    public void updatePost(Long userId, Long postId, PostRequestDto requestDto){
        Post post = getPost(postId);
        User user = getUser(userId);
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(user, post);
        // 변경된 필드만 반영
        if (requestDto.getChildCategoryId() != null){
            post.changeCategory(getCategory(requestDto.getChildCategoryId()));
        }
        if (requestDto.getPlatformId() != null){
            post.changePlatform(getPlatform(requestDto.getPlatformId()));
        }
        if (requestDto.getLanguageId() != null){
            post.changeLanguage(getLanguage(requestDto.getLanguageId()));
        }
        // 제목 및 내용은 단순 문자열로 외부 엔티티 조회 필요없이 값만 변경 (엔티티가 직접 Null 체크)
        post.updateTitle(requestDto.getPostTitle());
        post.updateContent(requestDto.getPostContent());
        // JPA는 dirty checking을 통해 상태변경을 감지하므로, Repository의 save() 호출이 필요없음
        // postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId){
        Post post = getPost(postId);
        User user = getUser(userId);
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(user, post);
        postRepository.delete(post);
    }

    public PostResponseDto getPostById(Long postId){
        PostResponseDto responseDto = postMapper.selectPost(postId);
        if (responseDto == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return responseDto;
    }

    public List<PostSummaryDto> getPostsByFilters(PostFilterRequestDto filterRequestDto){
        validateFilterCondition(filterRequestDto);
        List<PostSummaryDto> posts = postMapper.selectPostsByFilters(filterRequestDto);
        // 조회 API에서 빈 리스트는 예외가 아닌 빈 배열 반환이 더 적합함
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    private void validatePostOwnership(User user, Post post){
        // JPA 엔티티는 equals()를 재정의하여 식별자 비교로 소유자 확인시 효율성을 높임
        if(!post.getUser().equals(user)){
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

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
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
