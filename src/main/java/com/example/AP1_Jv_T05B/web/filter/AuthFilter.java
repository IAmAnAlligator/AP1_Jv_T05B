package com.example.AP1_Jv_T05B.web.filter;

import com.example.AP1_Jv_T05B.security.jwt.JwtAuthentication;
import com.example.AP1_Jv_T05B.security.jwt.JwtProvider;
import com.example.AP1_Jv_T05B.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  public AuthFilter(JwtProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);

      if (!jwtProvider.validateAccessToken(token)) {
        SecurityContextHolder.clearContext();
        throw new BadCredentialsException("Invalid or expired access token");
      }

      var claims = jwtProvider.getAccessClaims(token);
      JwtAuthentication auth = JwtUtil.fromClaims(claims);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}
