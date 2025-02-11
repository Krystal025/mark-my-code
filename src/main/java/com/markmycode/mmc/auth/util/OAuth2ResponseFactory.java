package com.markmycode.mmc.auth.util;

import com.markmycode.mmc.auth.dto.GoogleResponse;
import com.markmycode.mmc.auth.dto.OAuth2Response;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2ResponseFactory {

    public static OAuth2Response getOAuth2Response(String registrationId, Map<String, Object> attributes){
        switch (registrationId.toUpperCase()){
            case "GOOGLE":
                return new GoogleResponse(attributes);
            default:
                throw new OAuth2AuthenticationException("Unsupported social provider: " + registrationId);
        }
    }
}
