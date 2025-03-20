package com.markmycode.mmc.config;

import com.markmycode.mmc.auth.jwt.filter.AuthorizationFilter;
import com.markmycode.mmc.auth.jwt.provider.JwtTokenProvider;
import com.markmycode.mmc.auth.oauth.handler.OAuth2SuccessHandler;
import com.markmycode.mmc.auth.oauth.service.CustomOAuth2UserService;
import com.markmycode.mmc.auth.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration // Spring 설정 클래스로 지정
@EnableWebSecurity // Spring Security 설정 활성화 (사용자 정의 보안설정을 위함)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
                // 세션을 사용하지 않고 Stateless 방식으로 설정하여 서버가 세션을 생성하거나 저장하지 않도록 함
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 폼 로그인 방식 대신 API 기반의 인증방식을 사용하기 때문에 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 토큰 기반 인증을 사용하기 때문에 기본 인증을 통해 검증하지 않도록 비활성화
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/auth/**", "/login", "/oauth2/callback", "/oauth2/authorization", "/users/signup", "/login_success").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/comments/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated());
        http
                .oauth2Login(oauth2 -> oauth2
                        // 제공자로부터 Access 토큰을 받은 후 사용자 정보(e.g. 이메일, 이름)를 가져오기 위한 엔드포인트 설정
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        // .defaultSuccessUrl("/", true) // OAuth 로그인 성공 후 "/"로 이동
                );
        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String queryString = request.getQueryString();
                            String originalUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");
                            String redirectUrl = "/auth/login?redirect=" + URLEncoder.encode(originalUrl, StandardCharsets.UTF_8);
                            response.sendRedirect(redirectUrl);
                        })
                );
        http
                .addFilterBefore(new AuthorizationFilter(jwtTokenProvider, tokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
