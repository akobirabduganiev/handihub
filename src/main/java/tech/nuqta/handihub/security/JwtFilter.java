package tech.nuqta.handihub.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Performs the filter logic for authentication and authorization.
     *
     * @param request      the HTTP servlet request
     * @param response     the HTTP servlet response
     * @param filterChain the filter chain for executing subsequent filters
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            if (request.getServletPath().contains("/api/v1/auth")) {
                filterChain.doFilter(request, response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No token provided");
                return;
            }
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (!jwtService.isTokenValid(jwt, userDetails)) {
                    writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }
                if (jwtService.isRefreshToken(jwt)) {
                    writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Refresh token not allowed!");
                    return;
                }
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (Exception e) {
            writeJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
        }
    }

    /**
     * Checks if the given request should not be filtered.
     *
     * @param request The HttpServletRequest object representing the current request.
     * @return true if the request should not be filtered, false otherwise.
     * @throws ServletException if an error occurs while filtering the request.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
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
    /**
     * Writes JSON response with the given status and error message to the HttpServletResponse object.
     *
     * @param response     the HttpServletResponse object to write the response to
     * @param status       the HTTP status code for the response
     * @param errorMessage the error message to include in the response
     * @throws IOException if an I/O error occurs while writing the response
     */
    private void writeJsonResponse(HttpServletResponse response, int status, String errorMessage) throws IOException{
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \""+ errorMessage + "\"}");
    }
}
