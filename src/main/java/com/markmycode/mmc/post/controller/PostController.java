package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.language.dto.LanguageResponseDto;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.dto.PlatformResponseDto;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dto.PostFilterRequestDto;
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
    public String getFilteredPosts(@ModelAttribute PostFilterRequestDto requestDto, Model model) {
        // 필터 목록 조회
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        List<CategoryResponseDto> childCategories = new ArrayList<>(); // 부모 카테고리 선택 후 프론트에서 동적으로 변경
        if (requestDto.getParentCategoryId() != null) {
            childCategories = categoryService.getChildCategories(requestDto.getParentCategoryId()); // 부모 카테고리에 맞는 하위 카테고리
        }
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();
        List<PostSummaryDto> posts = postService.getPostsByFilters(requestDto);
        // 조회된 목록을 모델에 추가하여 뷰에서 사용할 수 있도록 전달
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);
        model.addAttribute("posts", posts);
        model.addAttribute("selectedParentCategoryId", requestDto.getParentCategoryId());
        model.addAttribute("selectedChildCategoryId", requestDto.getChildCategoryId());
        model.addAttribute("selectedPlatformId", requestDto.getPlatformId());
        model.addAttribute("selectedLanguageId", requestDto.getLanguageId());
        // 템플릿 렌더링하여 반환
        return "posts/list";
    }

    @GetMapping("/{postId}")
    public String getPostDetail(@PathVariable("postId") Long postId, Model model){
        PostResponseDto post = postService.getPostById(postId);
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/form")
    public String getPostForm(Model model){
        // 목록 조회는 기존과 동일한 서비스 메소드를 활용
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        // 자식 카테고리는 선택된 부모에 따라 동적으로 변경할 수 있으므로 초기에는 빈 리스트로 처리
        List<CategoryResponseDto> childCategories = new ArrayList<>();
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();

        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);
        // 게시글 작성을 위한 빈 DTO 추가
        model.addAttribute("postRequestDto", new PostRequestDto());

        return "posts/form";
    }

    @PostMapping
    public String createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                             @ModelAttribute PostRequestDto postRequestDto) {
        postService.createPost(userPrincipal.getUserId(), postRequestDto);
        return "redirect:/posts";
    }
}
