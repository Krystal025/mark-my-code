package com.markmycode.mmc.like.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.like.service.PostLikeService;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @PathVariable("postId") Long postId){
        postLikeService.likePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post liked");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("postId") Long postId){
        postLikeService.unlikePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post unliked");
    }

    @GetMapping
    public List<PostSummaryDto> getLikedPosts(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return postLikeService.getLikedPosts(userPrincipal.getUserId());
    }
}
