package com.markmycode.mmc.auth.oauth.service;

import com.markmycode.mmc.auth.oauth.dto.CustomOAuth2User;
import com.markmycode.mmc.auth.oauth.dto.OAuth2Response;
import com.markmycode.mmc.auth.oauth.dto.OAuth2UserInfo;
import com.markmycode.mmc.user.entity.User;
import com.markmycode.mmc.user.enums.Provider;
import com.markmycode.mmc.user.enums.Role;
import com.markmycode.mmc.user.repository.UserRepository;
import com.markmycode.mmc.util.EmailUtils;
import com.markmycode.mmc.util.OAuth2ResponseFactory;
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
        // OAuth2에서 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // Factory 패턴을 사용해 OAuth2Response 객체 생성
        OAuth2Response oAuth2Response = OAuth2ResponseFactory.getOAuth2Response(registrationId, oAuth2User.getAttributes());
        // 소셜 제공자 이름과 사용자 식별 ID를 조합하여 고유 ID 생성
        String socialId = oAuth2Response.getSocialProvider() + " " + oAuth2Response.getSocialProviderId();
        // DB에서 사용자 조회
        User existingUser = userRepository.findBySocialId(socialId);
        if(existingUser == null){
            // 신규 사용자 등록
            User user = createUser(oAuth2Response, socialId);
            return createOAuth2User(user);
        } else{
            // 기존 사용자 정보 업데이트
            updateUser(existingUser, oAuth2Response);
            return createOAuth2User(existingUser);
        }
    }

    private User createUser(OAuth2Response oAuth2Response, String socialId){
        User user = User.builder()
                .userName(oAuth2Response.getUserName())
                .userEmail(EmailUtils.normalizeEmail(oAuth2Response.getUserEmail()))
                .userNickname(oAuth2Response.getUserEmail()) // 임시 닉네임으로 이메일 사용 (추후 변경)
                .userRole(Role.ROLE_USER)
                .socialId(socialId)
                .socialProvider(Provider.valueOf(oAuth2Response.getSocialProvider()))
                .build();
        return userRepository.save(user);
    }

    private void updateUser(User user, OAuth2Response oAuth2Response) {
        user = user.toBuilder()
                .userName(oAuth2Response.getUserName() != null ? oAuth2Response.getUserName() : user.getUserName())
                .userEmail(oAuth2Response.getUserEmail() != null ? oAuth2Response.getUserEmail() : user.getUserEmail())
                .build();
        userRepository.save(user); // 수정된 사용자 정보 저장
    }

    private OAuth2User createOAuth2User(User user){
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .userNickname(user.getUserNickname())
                .userRole(user.getUserRole())
                .socialId(user.getSocialId())
                .socialProvider(user.getSocialProvider())
                .build();
        return CustomOAuth2User.fromOAuth2UserInfo(oAuth2UserInfo);

    }
}
