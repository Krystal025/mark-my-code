package com.markmycode.mmc.like.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.like.service.PostLikeService;
import com.markmycode.mmc.user.dto.UserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class PostLikeApiController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> toggleLikePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable("postId") Long postId){
        postLikeService.toggleLikePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post Like toggled");
    }

    @GetMapping("/{postId}")
    public List<UserSummaryDto> getUsersWhoLiked(@PathVariable("postId") Long postId){
        return postLikeService.getUsersWhoLiked(postId);
    }

}
