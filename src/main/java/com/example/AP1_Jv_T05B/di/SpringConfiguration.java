package com.example.AP1_Jv_T05B.di;

import com.example.AP1_Jv_T05B.security.jwt.JwtAccessDeniedHandler;
import com.example.AP1_Jv_T05B.security.jwt.JwtAuthenticationEntryPoint;
import com.example.AP1_Jv_T05B.web.filter.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@PropertySource("classpath:application-context.properties")
public class SpringConfiguration {

  private final AuthFilter authFilter;
  private final JwtAuthenticationEntryPoint authenticationEntryPoint;
  private final JwtAccessDeniedHandler accessDeniedHandler;

  public SpringConfiguration(
      AuthFilter authFilter,
      JwtAuthenticationEntryPoint authenticationEntryPoint,
      JwtAccessDeniedHandler accessDeniedHandler) {
    this.authFilter = authFilter;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  private static final String[] PUBLIC_ENDPOINTS = {
    "/",
    "/auth/login",
    "/auth/register",
    "/auth/token/refresh",
    "/auth/token/refresh-access",
    "/auth-client.js",
    "/index.html",
    "/index.js",
    "/game.html",
    "/game.js",
    "/game-view.html",
    "/game-view.js"
  };

  @Bean
  public SecurityFilterChain securityFilterChain(
      org.springframework.security.config.annotation.web.builders.HttpSecurity http)
      throws Exception {
    http
        // Отключаем CSRF для REST API
        .csrf(AbstractHttpConfigurer::disable)
        // Stateless сессии (JWT)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 🔥 ВАЖНО: обработка 401
        .exceptionHandling(
            ex ->
                ex.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))

        // Настраиваем доступ к endpoint'ам
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll().anyRequest().authenticated())
        // Добавляем фильтр JWT перед стандартным UsernamePasswordAuthenticationFilter
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // default 10, 12 более безопасно
  }
}
