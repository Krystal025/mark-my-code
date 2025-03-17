package com.markmycode.mmc;

import com.markmycode.mmc.auth.jwt.dto.CustomUserDetails;
import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;
    private final PostService postService;

    @GetMapping
    public String index(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userRepository.findByUserEmail(userDetails.getUserEmail());
            if (user != null) {
                CustomUserDetails enrichedUserDetails = CustomUserDetails.fromUser(user); // 닉네임 포함
                System.out.println("✅ loginUser: Email=" + enrichedUserDetails.getUserEmail() + ", Nickname=" + enrichedUserDetails.getUserNickname());
                model.addAttribute("loginUser", enrichedUserDetails);
            } else {
                model.addAttribute("loginUser", userDetails); // 기본값 유지
            }
        }
        List<PostSummaryDto> recentPosts = postService.getRecentPosts();
        List<PostSummaryDto> poplarPosts = postService.getPopularPosts();
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("popularPosts", poplarPosts);
        return "index";
    }

}
