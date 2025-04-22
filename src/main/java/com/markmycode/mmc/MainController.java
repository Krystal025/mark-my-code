package com.markmycode.mmc;

import com.markmycode.mmc.post.dto.PostPreviewResponseDto;
import com.markmycode.mmc.post.service.PostService;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping("/")
    public String index(Model model){
        List<PostPreviewResponseDto> recentPosts = postService.getRecentPosts();
        List<PostPreviewResponseDto> poplarPosts = postService.getPopularPosts();
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("popularPosts", poplarPosts);
        return "index";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }

}
