package com.markmycode.mmc.user.controller;

import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final PostService postService;

    // 내 게시글 목록 엔드포인트
//    @GetMapping("/{userId}/posts")
//    public ResponseEntity<PagedPostResponseDto> getUserPosts(@PathVariable("userId") Long userId,
//                                                             @ModelAttribute PostListRequestDto requestDto) {
//        return ResponseEntity.ok(postService.getUserPosts(userId, requestDto));
//    }
//
//    @GetMapping("/{userId}/liked-posts")
//    public ResponseEntity<PagedPostResponseDto> getUserLikedPosts(@PathVariable("userId") Long userId,
//                                                                  @ModelAttribute PostListRequestDto requestDto) {
//        return ResponseEntity.ok(postService.getLikedPostsByUserId(userId, requestDto));
//    }

//    @PostMapping("/signup")
//    public ResponseEntity<String> createUser(@RequestBody UserRequestDto userRequestDto){
//        userService.createUser(userRequestDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body("User registered");
//    }
//
//    @PatchMapping("/{userId}")
//    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
//                                             @PathVariable("userId") Long userId,
//                                             @RequestBody UserUpdateDto requestDto){
//        userService.updateUser(userPrincipal.getUserId(), userId, requestDto);
//        return ResponseEntity.ok("User Information Updated");
//    }
//
//
//    @PatchMapping("/{userId}/deactivate")
//    public ResponseEntity<String> deactivateUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
//                                                 @PathVariable("userId") Long userId,
//                                                 HttpServletResponse response){
//        userService.deactivateUser(userPrincipal.getUserId(), userId, response);
//        return ResponseEntity.ok("User Deactivated");
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<UserResponseDto> getUser(@PathVariable("userId") Long userId){
//        UserResponseDto userResponseDto = userService.getUserById(userId);
//        return ResponseEntity.ok(userResponseDto);
//    }

}
