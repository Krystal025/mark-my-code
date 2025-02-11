package com.markmycode.mmc.platform.service;

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

}
