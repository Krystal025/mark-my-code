package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.category.dto.CategoryResponseDto;
import com.markmycode.mmc.category.service.CategoryService;
import com.markmycode.mmc.comment.dto.CommentRequestDto;
import com.markmycode.mmc.comment.dto.CommentResponseDto;
import com.markmycode.mmc.comment.service.CommentService;
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
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final LanguageService languageService;

    // 게시글 등록
    @PostMapping
    public String create(@AuthenticationPrincipal UserPrincipal userPrincipal,
                         @ModelAttribute PostRequestDto postRequestDto) {
        Long postId = postService.createPost(userPrincipal.getUserId(), postRequestDto);
        return "redirect:/posts/" + postId;
    }

    // 게시글 수정
    @PostMapping("/{postId}/update")
    public String update(@AuthenticationPrincipal UserPrincipal userPrincipal,
                         @PathVariable("postId") Long postId,
                         @ModelAttribute PostRequestDto requestDto){
        postService.updatePost(userPrincipal.getUserId(), postId, requestDto);
        return "redirect:/posts/" + postId;
    }

    // 게시글 삭제
    @DeleteMapping("{postId}")
    public String delete(@AuthenticationPrincipal UserPrincipal userPrincipal,
                         @PathVariable("postId") Long postId) {
        postService.deletePost(userPrincipal.getUserId(), postId);
        return "redirect:/posts";
    }

    // 댓글 등록
    @PostMapping("/{postId}/comments")
    public String createComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("postId") Long postId,
                                @ModelAttribute CommentRequestDto requestDto) {
        if (userPrincipal == null) {
            return "redirect:/auth/login"; // 비회원 접근 시 로그인 페이지로 리다이렉트
        }
        commentService.createComment(userPrincipal.getUserId(), postId, requestDto);
        return "redirect:/posts/" + postId;
    }

    // 게시글 목록 조회 (필터링 포함)
    @GetMapping
    public String getList(@ModelAttribute PostListRequestDto requestDto, Model model) {
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

    // 게시글 상세페이지 조회
    @GetMapping("/{postId}")
    public String getDetail(@AuthenticationPrincipal UserPrincipal userPrincipal,
                            @PathVariable("postId") Long postId,
                            Model model){
        PostResponseDto post = postService.getPostById(postId);
        List<CommentResponseDto> comments = commentService.getComments(postId);
        boolean isAuthor = userPrincipal != null && post.getUserId().equals(userPrincipal.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("requestDto", new CommentRequestDto()); // 빈 DTO 추가
        return "posts/detail";
    }

    // 게시글 작성 폼 조회
    @GetMapping("/new")
    public String getNewForm(Model model){
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
        return "posts/new";
    }

    // 게시글 수정 폼 조회
    @GetMapping("/{postId}/edit")
    public String getEditForm(@PathVariable("postId") Long postId, Model model) {
        // 기존 게시글 조회 (PostResponseDto에는 userId, childCategoryId, parentCategoryId 등 필요)
        PostResponseDto post = postService.getPostById(postId);

        PostRequestDto requestDto = PostRequestDto.builder()
                .parentCategoryId(post.getParentCategoryId())
                .childCategoryId(post.getChildCategoryId())
                .platformId(post.getPlatformId())
                .languageId(post.getLanguageId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .build();

        List<CategoryResponseDto> parentCategories = categoryService.getParentCategories();
        List<CategoryResponseDto> childCategories = categoryService.getChildCategories(post.getParentCategoryId());
        List<PlatformResponseDto> platforms = platformService.getPlatforms();
        List<LanguageResponseDto> languages = languageService.getLanguages();

        model.addAttribute("post", post);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("postId", postId);
        model.addAttribute("parentCategories", parentCategories);
        model.addAttribute("childCategories", childCategories);
        model.addAttribute("platforms", platforms);
        model.addAttribute("languages", languages);

        return "posts/edit";
    }


}
