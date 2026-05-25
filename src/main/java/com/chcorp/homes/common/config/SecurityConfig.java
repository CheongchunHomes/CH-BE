package com.chcorp.homes.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String[] WHITE_LIST = {
            "/auth/login",
            "/auth/refresh",
            "/auth/reauth",
            "/auth/logout",
            "/users/register",
            "/admin",
            "/admin/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/diagnosis/simulate",
            "/announcements",
            "/announcements/**",
            "/properties/**",
            "/subscription",
            "/map",
            "/ws/map-chat",
            "/ws/map-chat/**",
            "/policies",
            "/policies/**",
            "/simulator/**"
//            ,"/**"   // 임시
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)      //JWT를 쓸 것이므로 CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) //Spring Security 기본 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) //JWT를 쓸 것이므로 Basic 인증은 사용 x
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )       // 세션을 만들지 않도록 설정, Stateless 로
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.POST, "/announcements/fetch/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/policies/fetch/*").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
