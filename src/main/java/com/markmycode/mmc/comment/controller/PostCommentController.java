package com.markmycode.mmc.comment.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.comment.dto.CommentRequestDto;
import com.markmycode.mmc.comment.dto.CommentResponseDto;
import com.markmycode.mmc.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostCommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("/{postId}/comments")
    public String createComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("postId") Long postId,
                                @ModelAttribute CommentRequestDto requestDto,
                                RedirectAttributes redirectAttributes) {
        commentService.createComment(userPrincipal.getUserId(), postId, requestDto);
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }

    // 댓글 수정
    @PostMapping("/{postId}/comments/{commentId}/update")
    public String updateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                @RequestParam("commentContent") String commentContent,
                                RedirectAttributes redirectAttributes) {
        // 댓글 수정 서비스 호출
        commentService.updateComment(userPrincipal.getUserId(), commentId, commentContent);
        // 리다이렉트 시 postId 전달
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }

    // 댓글 삭제
    @PostMapping("/{postId}/comments/{commentId}/delete")
    public String deleteComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                @PathVariable("postId") Long postId,
                                @PathVariable("commentId") Long commentId,
                                RedirectAttributes redirectAttributes) {
        commentService.deactivateComment(userPrincipal.getUserId(), commentId);
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }

    @GetMapping("/comments/{parentId}/children")
    @ResponseBody
    public List<CommentResponseDto> getChildComments(@PathVariable("parentId") Long parentId) {
        return commentService.getChildComments(parentId);
    }

}
