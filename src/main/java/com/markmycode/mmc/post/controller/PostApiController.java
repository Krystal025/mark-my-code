package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.post.dto.PostListRequestDto;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostApiController {

    private final UserService userService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @RequestBody PostRequestDto requestDto){
        postService.createPost(userPrincipal.getUserId(), requestDto);
        return ResponseEntity.ok("Post created");
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("postId") Long postId,
                                             @RequestBody PostRequestDto requestDto){
        postService.updatePost(userPrincipal.getUserId(), postId, requestDto);
        return ResponseEntity.ok("Post updated");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("postId") Long postId){
        postService.deletePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post deleted");
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable("postId") Long postId){
        return postService.getPostById(postId);
    }

    @GetMapping
    public List<PostSummaryDto> getFilteredPosts(@RequestBody PostListRequestDto requestDto){
        return postService.getFilteredPosts(requestDto);
    }

}
