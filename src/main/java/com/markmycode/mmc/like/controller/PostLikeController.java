package com.markmycode.mmc.like.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.like.service.PostLikeService;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.user.dto.UserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> toggleLikePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @PathVariable("postId") Long postId){
        postLikeService.toggleLikePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post Like toggled");
    }

    @GetMapping
    public List<PostSummaryDto> getLikedPosts(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return postLikeService.getLikedPosts(userPrincipal.getUserId());
    }

    @GetMapping("/{postId}")
    public List<UserSummaryDto> getUsersWhoLiked(@PathVariable("postId") Long postId){
        return postLikeService.getUsersWhoLiked(postId);
    }

}
