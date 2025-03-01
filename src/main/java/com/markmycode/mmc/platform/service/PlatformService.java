package com.markmycode.mmc.platform.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.BadRequestException;
import com.markmycode.mmc.platform.dto.PlatformResponseDto;
import com.markmycode.mmc.platform.entity.Platform;
import com.markmycode.mmc.platform.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;

    public List<PlatformResponseDto> getPlatforms(){
        List<Platform> platforms = platformRepository.findAll();
        return platforms.stream()
                .map(c -> new PlatformResponseDto(c.getPlatformName()))
                .toList();
    }

    // 유효성 검사
    public void validatePlatform(Integer platformId) {
        if (platformId != null && !platformRepository.existsById(platformId)) {
            throw new BadRequestException(ErrorCode.INVALID_PLATFORM);
        }
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public Platform getPlatform(Integer platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_PLATFORM));
    }
}
