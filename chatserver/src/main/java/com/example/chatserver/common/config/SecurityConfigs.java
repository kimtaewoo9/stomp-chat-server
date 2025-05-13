package com.example.chatserver.common.config;

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
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            // 인증된 사용자 -> Authentication 객체가 SecurityContextHolder에 저장되어 있는 사용자 ..
            .authorizeHttpRequests(
                a -> a.requestMatchers("/member/create", "/member/doLogin", "/connect/**")
                    .permitAll(). // 이 경로들은 authentication 객체가 없어도 허용
                        anyRequest().
                    authenticated() // 나머지 요청들은 .. 인증 되어야함 .
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // UsernamePasswordAuthenticationFilter -> 로그인 요청시 아이디 비밀번호가 맞는지 확인 .
            // 근데 여기서 401 오류 내는거 같음 ..
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "https://stomp-chat-server.up.railway.app"));
        configuration.setAllowedMethods(List.of("*")); // 모든 Http 메서드 허용
        configuration.setAllowedHeaders(List.of("*")); // 모든 header 값 허용 .
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용 !.

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url 패턴에 대해 .. cors 허용

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
