package com.markmycode.mmc.language.controller;

import com.markmycode.mmc.language.dto.LanguageResponseDto;
import com.markmycode.mmc.language.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @GetMapping
    public List<LanguageResponseDto> getLanguages(){
        return languageService.getLanguages();
    }

}
