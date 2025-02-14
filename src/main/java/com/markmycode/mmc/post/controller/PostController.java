package com.markmycode.mmc.post.controller;

import com.markmycode.mmc.auth.model.UserPrincipal;
import com.markmycode.mmc.post.dto.PostRequestDto;
import com.markmycode.mmc.post.dto.PostResponseDto;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @RequestBody PostRequestDto postDto){
        postService.createPost(userPrincipal.getUserId(), postDto);
        return ResponseEntity.ok("Post created");
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("postId") Long postId,
                                             @RequestBody PostRequestDto postDto){
        postService.updatePost(userPrincipal.getUserId(), postId, postDto);
        return ResponseEntity.ok("Post updated");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @PathVariable("postId") Long postId){
        postService.deletePost(userPrincipal.getUserId(), postId);
        return ResponseEntity.ok("Post deleted");
    }

    @GetMapping("/list")
    public List<PostSummaryDto> getPostList(){
        return postService.getPostList();
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                   @PathVariable("postId") Long postId){
        return postService.getPost(postId);
    }

}
