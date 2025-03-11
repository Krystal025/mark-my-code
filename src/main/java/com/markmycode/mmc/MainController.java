package com.markmycode.mmc;

import com.markmycode.mmc.post.dto.PostSummaryDto;
import com.markmycode.mmc.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    @GetMapping
    public String index(Model model){
        List<PostSummaryDto> recentPosts = postService.getRecentPosts();
        List<PostSummaryDto> poplarPosts = postService.getPopularPosts();
        model.addAttribute("recentPosts", recentPosts);
        model.addAttribute("popularPosts", poplarPosts);
        return "index";
    }

}
