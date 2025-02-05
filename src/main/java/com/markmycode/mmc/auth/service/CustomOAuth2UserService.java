package com.markmycode.mmc.auth.service;

import com.markmycode.mmc.auth.dto.*;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Provider;
import com.markmycode.mmc.user.enums.Role;
import com.markmycode.mmc.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// OAuth2 로그인 성공 후, Access Token을 이용해 소셜 API에서 사용자 정보를 가져옴
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 소셜 로그인 성공시 호출되는 메소드 (사용자 등록/업데이트 및 인증/인가에 사용될 사용자 정보 객체 반환)
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("OAuth2UserRequest: " + userRequest);
        // OAuth2에서 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 어떤 소셜 로그인 제공자에서 요청이 왔는지 확인
        OAuth2Response oAuth2Response = null;
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Social Provider: " + registrationId);
        // 제공자가 Google인 경우 사용자 데이터를 GoogleResponse 객체로 변환
        if (registrationId.equals("GOOGLE")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Google Login Failed");
        }
        // 소셜 제공자 이름과 사용자 식별 ID를 조합하여 고유 ID 생성
        String socialId = oAuth2Response.getSocialProvider() + " " + oAuth2Response.getSocialProviderId();
        // OAuth2RequestDto로 변환 (DTO에서 검증할 수 있도록 함)
        OAuth2RequestDto oAuth2RequestDto = OAuth2RequestDto.builder()
                .userName(oAuth2Response.getUserName())
                .userEmail(oAuth2Response.getUserEmail())
                .userNickname(oAuth2Response.getUserName())
                .build();
        // OAuth2 로그인을 통해 사용자 정보를 받아 해당 사용자가 존재하는지 확인
        User existData = userRepository.findBySocialId(socialId);
        // 새로운 사용자일 경우 User 엔티티를 생성하여 DB에 저장
        if(existData == null){
            User user = User.builder()
                    .userName(oAuth2RequestDto.getUserName())
                    .userEmail(oAuth2RequestDto.getUserEmail())
                    .userNickname(oAuth2RequestDto.getUserNickname()) // 기본 닉네임으로 사용자 이름 사용
                    .socialId(socialId)
                    .socialProvider(Provider.valueOf(oAuth2Response.getSocialProvider()))
                    .build();
            userRepository.save(user);
            // OAuth2 사용자 정보를 애플리케이션에서 사용할 객체로 변환
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .userEmail(user.getUserEmail())
                    .userRole(Role.ROLE_USER)
                    .socialId(socialId)
                    .socialProvider(Provider.valueOf(oAuth2Response.getSocialProvider()))
                    .build();
        return new CustomOAuth2User(oAuth2UserInfo); // Spring Security에서 사용할 객체
    } else{
            // 변경된 정보가 있을 경우 업데이트
            boolean isUpdated = false;
            if (!existData.getUserName().equals(oAuth2RequestDto.getUserName())) {
                existData.setUserName(oAuth2RequestDto.getUserName());
                isUpdated = true;
            }
            if (!existData.getUserEmail().equals(oAuth2RequestDto.getUserEmail())) {
                existData.setUserEmail(oAuth2RequestDto.getUserEmail());
                isUpdated = true;
            }
            if (isUpdated) {
                userRepository.save(existData); // 수정된 사용자 정보 저장
            }
            OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
                    .userId(existData.getUserId())
                    .userName(oAuth2RequestDto.getUserName())
                    .userEmail(oAuth2RequestDto.getUserEmail())
                    .userRole(existData.getUserRole())
                    .socialId(existData.getSocialId())
                    .socialProvider(existData.getSocialProvider())
                    .build();
            return new CustomOAuth2User(oAuth2UserInfo);
        }
    }
}
