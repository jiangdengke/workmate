package org.jdk.workmate.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      try {
        DecodedJWT jwt = jwtService.verify(token);
        long userId = Long.parseLong(jwt.getSubject());
        String userName = jwt.getClaim("un").asString();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
          var principal = new LoginUser(userId, userName, "");
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
          var authToken =
              new UsernamePasswordAuthenticationToken(principal, null, authorities);
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception ignore) {
        // invalid token -> skip, downstream will get 401 if endpoint requires auth
      }
    }
    filterChain.doFilter(request, response);
  }
}
