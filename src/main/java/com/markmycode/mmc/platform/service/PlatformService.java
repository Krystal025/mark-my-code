package com.markmycode.mmc.platform.service;

import com.markmycode.mmc.exception.ErrorCode;
import com.markmycode.mmc.exception.custom.NotFoundException;
import com.markmycode.mmc.platform.entity.Platform;
import com.markmycode.mmc.platform.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;

    public List<Platform> getPlatformList(){
        return platformRepository.findAll();
    }

    // 유효성 검사
    public void validatePlatform(Integer platformId) {
        if (platformId != null && !platformRepository.existsById(platformId)) {
            throw new NotFoundException(ErrorCode.PLATFORM_NOT_FOUND);
        }
    }

    // 해당 ID에 대한 엔티티 객체 반환
    public Platform getPlatform(Integer platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PLATFORM_NOT_FOUND));
    }
}
