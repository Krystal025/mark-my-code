package com.markmycode.mmc.platform.controller;

import com.markmycode.mmc.platform.dto.PlatformResponseDto;
import com.markmycode.mmc.platform.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping
    public List<PlatformResponseDto> getPlatforms(){
        return platformService.getPlatforms();
    }

}
