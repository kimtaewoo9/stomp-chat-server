package com.example.chatserver.common.configs;

import com.example.chatserver.common.auth.JwtAuthFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigs {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화 토큰 기반 인증을 사용하므로 필요 없음 .
            .httpBasic(
                AbstractHttpConfigurer::disable) // 사용자 이름과 비밀번호를 Base64로 인코딩 하여 전송하는 것 .. 필요 없음 .
            .authorizeHttpRequests(
                a -> a.requestMatchers("/member/create", "/member/doLogin", "/connect/**")
                    .permitAll().anyRequest().authenticated())
            .sessionManagement(s -> s.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)) // 세션을 사용하여 상태를 저장하는 것을 하지 않음 .
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class) // 사실상 이게 핵심 .. 이거 구현 해야함 .
            .build();
    }

    @Bean
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("*")); // 모든 Http 메서드 허용
        configuration.setAllowedHeaders(List.of("*")); // 모든 header 값 허용 .
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용 !.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url 패턴에 대해 .. cors 허용

        return source;
    }

    @Bean
    public PasswordEncoder makePassword() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
