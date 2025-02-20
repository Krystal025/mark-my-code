package com.markmycode.mmc.language.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.language.entity.Language;
import com.markmycode.mmc.language.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private LanguageRepository languageRepository;

    public List<Language> getLanguageList(){
        return languageRepository.findAll();
    }

    public void validateLanguage(Integer languageId) {
        if (languageId != null && !languageRepository.existsById(languageId)) {
            throw new NotFoundException(ErrorCode.LANGUAGE_NOT_FOUND);
        }
    }
}