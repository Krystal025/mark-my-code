package com.markmycode.mmc.language.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
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

    // 유효성 검사
    public void validateLanguage(Integer languageId) {
        if (languageId != null && !languageRepository.existsById(languageId)) {
            throw new BadRequestException(ErrorCode.INVALID_LANGUAGE);
        }
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public Language getLanguage(Integer languageId) {
        return languageRepository.findById(languageId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_LANGUAGE));
    }
}