package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.language.dto.LanguageResponseDto;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.dto.PlatformResponseDto;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dto.PostListRequestDto;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final LanguageService languageService;

    @GetMapping
    public String getPostList(@ModelAttribute PostListRequestDto requestDto, Model model) {
        // 필터 목록 조회
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        List<CategoryResponseDto> childCategories = new ArrayList<>(); // 부모 카테고리 선택 후 프론트에서 동적으로 변경
        if (requestDto.getParentCategoryId() != null) {
            childCategories = categoryService.getChildCategories(requestDto.getParentCategoryId()); // 부모 카테고리에 맞는 하위 카테고리
        }
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();
        List<PostSummaryDto> posts = postService.getFilteredPosts(requestDto);
        // 조회된 목록을 모델에 추가하여 뷰에서 사용할 수 있도록 전달
        model.addAttribute("posts", posts);
        model.addAttribute("requestDto" , requestDto);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);
        // 템플릿 렌더링하여 반환
        return "posts/list";
    }

    @GetMapping("/{postId}")
    public String getPostDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("postId") Long postId,
                                Model model){
        PostResponseDto post = postService.getPostById(postId);
        boolean isAuthor = post.getUserId().equals(userPrincipal.getUserId());

        model.addAttribute("post", post);
        model.addAttribute("isAuthor", isAuthor);

        return "posts/detail";
    }


    @PostMapping("/update/{postId}")
    public String updatePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable("postId") Long postId,
                             @ModelAttribute PostRequestDto requestDto){
        postService.updatePost(userPrincipal.getUserId(), postId, requestDto);
        return "redirect:/posts/" + postId;
    }

    @DeleteMapping("{postId}")
    public String deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @PathVariable("postId") Long postId) {
        postService.deletePost(userPrincipal.getUserId(), postId);
        return "redirect:/posts";
    }

    @GetMapping("/create-form")
    public String getPostForm(Model model){
        // 목록 조회는 기존과 동일한 서비스 메소드를 활용
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        // 자식 카테고리는 선택된 부모에 따라 동적으로 변경할 수 있으므로 초기에는 빈 리스트로 처리
        List<CategoryResponseDto> childCategories = new ArrayList<>();
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();

        // 게시글 작성을 위한 빈 DTO 추가
        model.addAttribute("requestDto", new PostRequestDto());
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);

        return "posts/create-form";
    }

    @GetMapping("/update-form/{postId}")
    public String getPostEditForm(@PathVariable("postId") Long postId, Model model) {
        // 기존 게시글 조회 (PostResponseDto에는 userId, childCategoryId, parentCategoryId 등 필요)
        PostResponseDto post = postService.getPostById(postId);

        // 수정 폼용 DTO를 빌더 패턴으로 생성 (컨트롤러에서는 최소한의 처리만)
        PostRequestDto requestDto = PostRequestDto.builder()
                .parentCategoryId(post.getParentCategoryId())
                .childCategoryId(post.getChildCategoryId())
                .platformId(post.getPlatformId())
                .languageId(post.getLanguageId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .build();

        // 부모 카테고리 목록 조회
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        List<CategoryResponseDto> childCategories = categoryService.getChildCategories(post.getParentCategoryId());
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();

        // 모델에 값 추가
        model.addAttribute("post", post);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("postId", postId);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);

        return "posts/update-form";
    }

    @PostMapping
    public String createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @ModelAttribute PostRequestDto postRequestDto) {
        Long postId = postService.createPost(userPrincipal.getUserId(), postRequestDto);
        return "redirect:/posts/" + postId;
    }
}
