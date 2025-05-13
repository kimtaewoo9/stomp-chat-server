package com.example.chatserver.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.sasl.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {

    private final String secretKey;

    public JwtAuthFilter(@Value("${jwt.secretKey}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String token = request.getHeader("Authorization");
        try {
            if (token != null) {
                if (!token.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                // 검증 및 claims 추출
                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    "",
                    userDetails.getAuthorities()); // 권한 정보가 포함된 인증 토큰 발급 .
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 토큰이 없으면 .. 넘어감 .
            chain.doFilter(servletRequest, servletResponse);
        }
        // 예외 처리 종류 .. 1. 토큰이 만료 된 경우 2. 유효하지 않은 토큰인 경우
        // 만약에 토큰이 없으면 다음 필터로 넘어감 .
        catch (ExpiredJwtException e) {
            log.warn("jwt expired");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"토큰이 만료되었습니다. 다시 로그인해주세요.\"}");
        } catch (JwtException e) {
            log.warn("invalid token");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"유효하지 않은 토큰입니다.\"}");
        } catch (AuthenticationException e) { // Bearer 형식 오류 등 (Spring Security 예외 사용)
            log.warn("Authentication format error");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected authentication error for URI: {}. Type: {}, Message: {}",
                request.getRequestURI(),
                e.getClass().getName(), // 오류 타입 (클래스 이름)
                e.getMessage(),        // 오류 메시지
                e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"인증에 실패했습니다.\"}");
        }
    }

}
