package com.markmycode.mmc.like.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.like.service.PostLikeService;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final UserService userService;
    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping("/{postId}/like")
    public String toggleLikePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                 @PathVariable("postId") Long postId,
                                 RedirectAttributes redirectAttributes){

        Long loginUserId = userPrincipal.getUserId();
        PostResponseDto post = postService.getPostById(postId); // DTO 유지
        redirectAttributes.addAttribute("postId", postId);
        if (loginUserId.equals(post.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "본인의 게시글에는 좋아요를 누를 수 없습니다.");
            return "redirect:/posts/{postId}";
        }
        postLikeService.toggleLikePost(loginUserId, postId);
        return "redirect:/posts/{postId}";

//        User user = userService.getUser(userPrincipal.getUserId());
//        Post post = postService.getPost(postId);
//        redirectAttributes.addAttribute("postId", postId);
//        try {
//            postService.validatePostOwnership(user, post);
//            redirectAttributes.addFlashAttribute("error", "본인의 게시글에는 좋아요를 누를 수 없습니다.");
//            return "redirect:/posts/{postId}";
//        }catch (ForbiddenException e){
//            postLikeService.toggleLikePost(userPrincipal.getUserId(), postId);
//            return "redirect:/posts/{postId}";
//        }
    }

}
