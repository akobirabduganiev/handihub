package tech.nuqta.handihub.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a filter that allows filtering and processing of JWT tokens in each request.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_API_PATH = "/api/v1/auth";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        try {
            if (isAuthRequest(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = resolveToken(request);
            if (jwt == null) {
                writeJsonResponse(response, "No Token provided");
                return;
            }

            final String userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                processUserAuthentication(request, response, jwt, userEmail);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            writeJsonResponse(response, "Token expired");
        } catch (Exception e) {
            writeJsonResponse(response, "Invalid Token: " + e.getMessage());
        }
    }

    private boolean isAuthRequest(HttpServletRequest request) {
        return request.getServletPath().contains(AUTH_API_PATH);
    }

    private String resolveToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private void processUserAuthentication(HttpServletRequest request, HttpServletResponse response, String jwt, String userEmail) throws IOException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        if (!jwtService.isTokenValid(jwt, userDetails)) {
            writeJsonResponse(response, "Invalid Token");
            return;
        }
        if (jwtService.isRefreshToken(jwt)) {
            writeJsonResponse(response, "Refresh Token not allowed!");
            return;
        }
        setUsernamePasswordAuthenticationToken(request, userDetails);
    }

    private void setUsernamePasswordAuthenticationToken(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        List<String> pathsToSkip = Arrays.asList(
                "/api/v1/auth/**",
                "/v2/api-docs",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html"
        );
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathsToSkip.stream().anyMatch(path -> pathMatcher.match(path, request.getServletPath()));
    }

    private void writeJsonResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + errorMessage + "\"}");
    }
}