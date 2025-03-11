package com.markmycode.mmc.post.service;

import com.markmycode.mmc.category.entity.Category;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.ForbiddenException;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.language.entity.Language;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.entity.Platform;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dto.*;
import com.markmycode.mmc.post.entity.Post;
import com.markmycode.mmc.post.repository.PostMapper;
import com.markmycode.mmc.post.repository.PostRepository;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;

    private final PostRepository postRepository;

    private final UserService userService;
    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final LanguageService languageService;

    public void createPost(Long userId, PostRequestDto requestDto){
        Integer parentCategoryId = postMapper.selectParentIdByCategoryId(requestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, requestDto.getChildCategoryId());
        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(requestDto.getChildCategoryId());
        Platform platform = platformService.getPlatform(requestDto.getPlatformId());
        Language language = languageService.getLanguage(requestDto.getLanguageId());
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
        User user = userService.getUser(userId);
        Post post = getPost(postId);
        validatePostOwnership(user, post);
        // 변경된 필드만 반영
        if (requestDto.getChildCategoryId() != null){
            post.changeCategory(categoryService.getCategory(requestDto.getChildCategoryId()));
        }
        if (requestDto.getPlatformId() != null){
            post.changePlatform(platformService.getPlatform(requestDto.getPlatformId()));
        }
        if (requestDto.getLanguageId() != null){
            post.changeLanguage(languageService.getLanguage(requestDto.getLanguageId()));
        }
        if(requestDto.getPostTitle() != null){
            post.updateTitle(requestDto.getPostTitle());
        }
        if(requestDto.getPostContent() != null){
            post.updateContent(requestDto.getPostContent());
        }
        // JPA dirty checking으로 DB에 자동 반영
    }

    @Transactional
    public void deletePost(Long userId, Long postId){
        User user = userService.getUser(userId);
        Post post = getPost(postId);
        // 게시글 소유자가 요청한 사용자와 일치하는지 확인
        validatePostOwnership(user, post);
        postRepository.delete(post);
    }

    public List<PostSummaryDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getPostId())
                        .postTitle(post.getPostTitle())
                        .postCreatedAt(post.getPostCreatedAt())
                        .postLikeCount(post.getPostLikeCount())
                        .userNickname(post.getUser().getUserNickname())
                        .build())
                .collect(Collectors.toList());
    }

    public PostResponseDto getPostById(Long postId){
        PostResponseDto responseDto = postMapper.selectPost(postId);
        if (responseDto == null) {
            throw new NotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return responseDto;
    }

    public List<PostSummaryDto> getFilteredPosts(PostListRequestDto requestDto){
        validateFilterCondition(requestDto);
        List<PostSummaryDto> posts = postMapper.selectPostsByFilters(requestDto);
        // 조회 API에서 빈 리스트는 예외가 아닌 빈 배열 반환이 더 적합함
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    public List<PostSummaryDto> getRecentPosts(){
        List<PostSummaryDto> posts = postMapper.selectRecentPosts();
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    public List<PostSummaryDto> getPopularPosts(){
        List<PostSummaryDto> posts = postMapper.selectPopularPosts();
        return posts.isEmpty() ? Collections.emptyList() : posts;
    }

    // 필터링 조건 유효성 검사
    private void validateFilterCondition(PostListRequestDto requestDto){
        Integer parentCategoryId = postMapper.selectParentIdByCategoryId(requestDto.getChildCategoryId());
        categoryService.validateCategory(parentCategoryId, requestDto.getChildCategoryId());
        platformService.validatePlatform(requestDto.getPlatformId());
        languageService.validateLanguage(requestDto.getLanguageId());
    }

    // 게시글 작성자 유효성 검사
    private void validatePostOwnership(User user, Post post){
        // JPA 엔티티는 equals()를 재정의하여 식별자 비교로 소유자 확인시 효율성을 높임
        if(!Objects.equals(user.getUserId(), post.getUser().getUserId())){
            throw new ForbiddenException(ErrorCode.USER_NOT_MATCH);
        }
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
    }

}
