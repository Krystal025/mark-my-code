package com.markmycode.mmc.comment.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.comment.dto.CommentRequestDto;
import com.markmycode.mmc.comment.dto.CommentResponseDto;
import com.markmycode.mmc.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> createComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable("postId") Long postId,
                                                @RequestBody CommentRequestDto requestDto){
        commentService.createComment(userPrincipal.getUserId(), postId, requestDto);
        return ResponseEntity.ok("Comment created");
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                @PathVariable("commentId") Long commentId,
                                                @RequestBody CommentRequestDto requestDto){
        commentService.updateComment(userPrincipal.getUserId(), commentId, requestDto);
        return ResponseEntity.ok("Comment updated");
    }

    @PatchMapping("/{commentId}/deactivate")
    public ResponseEntity<String> deactivateComment(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                    @PathVariable("commentId") Long commentId){
        commentService.deactivateComment(userPrincipal.getUserId(), commentId);
        return ResponseEntity.ok("Comment deactivated");
    }

    @GetMapping("/{postId}")
    public List<CommentResponseDto> getComments(@PathVariable("postId")Long postId){
        return commentService.getComments(postId);
    }
}
