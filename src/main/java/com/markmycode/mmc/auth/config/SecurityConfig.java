package com.markmycode.mmc.auth.config;

import com.markmycode.mmc.auth.filter.JwtAuthorizationFilter;
import com.markmycode.mmc.auth.service.JwtTokenProvider;
import com.markmycode.mmc.auth.filter.OAuth2AuthorizationFilter;
import com.markmycode.mmc.auth.service.CustomOAuth2UserService;
import com.markmycode.mmc.auth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@Configuration // Spring 설정 클래스로 지정
@EnableWebSecurity // Spring Security 설정 활성화 (사용자 정의 보안설정을 위함)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler OAuth2SuccessHandler;

//    // 사용자 인증(Authentication)을 처리하는 AuthenticationManager 반환
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    // 비밀번호를 암호화하는 BCryptPasswordEncoder를 Bean으로 등록하여 사용
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        http
                .cors((cors) -> cors // CORS 설정 활성화
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 도메인
                            configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 혀용
                            configuration.setAllowCredentials(true); // 쿠키 전송 허용
                            configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 요청 헤더 허용
                            configuration.setMaxAge(3600L); // CORS 요청에 대한 캐시 유효시간 설정 (1시간)
                            configuration.setExposedHeaders(Collections.singletonList("Set-Cookie")); // 서버가 응답헤더에 포함시킨 Set-Cookie를 클라이언트가 접근할 수 있도록 설정
                            configuration.setExposedHeaders(Collections.singletonList("Authorization")); // 클라이언트가 접근가능한 응답 헤더
                            return configuration;
                        }));
        http
                // 세션을 stateless로 관리하기 때문에 csrf 공격이 존재하지 않으므로 csrf 보안 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 폼 로그인 방식 대신 API 기반의 인증방식을 사용하기 때문에 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 토큰 기반 인증을 사용하기 때문에 기본 인증을 통해 검증하지 않도록 비활성화
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                // OAuth2 기반 로그인 활성화
                .oauth2Login((oauth2) -> oauth2
                        // 제공자로부터 Access 토큰을 받은 후 사용자 정보(e.g. 이메일, 이름)를 가져오기 위한 엔드포인트 설정
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                // 사용자 정보를 처리할 서비스 설정
                                .userService(customOAuth2UserService))
                        // OAuth 인증이 성공적으로 완료된 후 실행될 성공 핸들러 설정
                        .successHandler(OAuth2SuccessHandler));
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/**", "/oauth2/callback", "/user/signup", "/login_success", "/home", "/post/list").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());
        http
                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 실행하여 모든 요청의 JWT 유효성을 검사함
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                // OAuth2 로그인 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 실행하여 소셜 로그인 처리를 수행함
                .addFilterBefore(new OAuth2AuthorizationFilter(jwtTokenProvider, tokenService), UsernamePasswordAuthenticationFilter.class)
                // 세션을 사용하지 않고 Stateless 방식으로 설정하여 서버가 세션을 생성하거나 저장하지 않도록 함
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
