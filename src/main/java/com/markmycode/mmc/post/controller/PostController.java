package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.language.dto.LanguageResponseDto;
import com.markmycode.mmc.language.service.LanguageService;
import com.markmycode.mmc.platform.dto.PlatformResponseDto;
import com.markmycode.mmc.platform.service.PlatformService;
import com.markmycode.mmc.post.dto.PostFilterRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        // 부모 카테고리 ID가 null일 경우 기본값 설정
        Integer parentCategoryId = requestDto.getParentCategoryId();
        if (parentCategoryId == null) {
            parentCategoryId = -1;  // 기본값 설정 (상위 카테고리 ID가 없는 경우에 대한 처리)
        }
        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        List<CategoryResponseDto> childCategories = new ArrayList<>();
        List<PlatformResponseDto> platforms = platformService.getPlatforms(); // 플랫폼 리스트
        List<LanguageResponseDto> languages = languageService.getLanguages(); // 언어 리스트
        List<PostSummaryDto> posts = postService.getPostsByFilters(requestDto);

        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);
        model.addAttribute("posts", posts);
        return "posts"; // posts.html 템플릿 렌더링
    }

    @GetMapping("/{postId}")
    public String getPost(@PathVariable("postId") Long postId, Model model){
        PostResponseDto post = postService.getPostById(postId);
        model.addAttribute("post", post);
        return "postDetail";
    }
}
