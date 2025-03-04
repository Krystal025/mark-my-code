package com.markmycode.mmc.language.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LanguageResponseDto {

    private Integer languageId;
    private String languageName; // display_name

}
