package org.beaconfire.housing.filter; // Assuming this is your package

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // Exclude actuator, swagger-ui, and openapi-docs from this filter
    return path.startsWith("/actuator")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/openapi/api-docs");
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain)
          throws ServletException, IOException {

    String userId = request.getHeader("x-User-Id");
    String username = request.getHeader("x-Username");
    String rolesHeader = request.getHeader("x-Roles");

    log.info("userId = {}, username = {}, roles = {}", userId, username, rolesHeader);

    if (userId != null && username != null && rolesHeader != null) {
      List<GrantedAuthority> authorities =
              Arrays.stream(rolesHeader.split(","))
                      .map(SimpleGrantedAuthority::new)
                      .collect(Collectors.toList());
      UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(userId, null, authorities);
      auth.setDetails(username);
      SecurityContextHolder.getContext().setAuthentication(auth);
      filterChain.doFilter(request, response);
    } else {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
  }
}