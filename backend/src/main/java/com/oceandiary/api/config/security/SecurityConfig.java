package com.oceandiary.api.config.security;

import com.oceandiary.api.user.security.token.JwtAuthEntryPoint;
import com.oceandiary.api.user.security.token.JwtAuthenticationFilter;
import com.oceandiary.api.user.security.token.TokenProvider;
import com.oceandiary.api.user.security.userdetails.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 목적 : 서버에 보안 설정 적용(리소스 접근 가능 여부 세팅)
    private final TokenProvider tokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public JwtAuthenticationFilter tokenAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .httpBasic().disable()
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint)
                .and()
                .authorizeRequests() // 사용권한 체크
                .antMatchers("/docs/**").permitAll() // restdocs 주소는 누구나 접근 가능
                .antMatchers("/*/login", "/api/naver/**", "/api/kakao/**").permitAll() // 가입 및 인증 주소는 누구나 접근 가능
                .antMatchers("/api/user/login/**").permitAll() // 테스트는 누구나 접근 가능 (TODO: 삭제 필요)
                .antMatchers("/api/diary/user/**").permitAll()// TODO: 비로그인 관련 처리 필수
                .antMatchers("/api/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/rooms").authenticated()
                .antMatchers(HttpMethod.PATCH, "/api/rooms/*/info").authenticated()
                .antMatchers(HttpMethod.POST, "/api/rooms/*/participants/*").authenticated()
                .antMatchers("/api/rooms/**").permitAll()
                .antMatchers("/api/ws/**").permitAll()
                .anyRequest().authenticated() // 그외 나머지 요청은 모두 인증된 회원만 접근 가능
                .and()
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // jwt token 필터를 id/password 인증 필터 전에 추가 (TODO: oauth 설정 후 변경 필요)
    }
}
