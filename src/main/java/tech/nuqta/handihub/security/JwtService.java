package tech.nuqta.handihub.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * The JwtService class provides methods for JWT OTP generation, extraction, and validation.
 */
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long jwtRefreshExpiration;

    /**
     * Extracts the username from a JWT OTP.
     *
     * @param token The JWT OTP from which to extract the username.
     * @return The username extracted from the JWT OTP.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the given OTP using the provided claimsResolver.
     *
     * @param token          the OTP from which to extract the claim
     * @param claimsResolver the function used to resolve the claim from the claims object
     * @param <T>            the type of the claim to be extracted
     * @return the extracted claim of type T
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a refresh OTP with the specified extra claims and user details.
     *
     * @param extraClaims   The additional claims to include in the refresh OTP.
     * @param userDetails  The user details for whom the refresh OTP is being generated.
     * @return The generated refresh OTP as a string.
     */
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("token_type", "refresh");
        return buildToken(extraClaims, userDetails, jwtRefreshExpiration);
    }

    /**
     * Generates a OTP for the provided extra claims and user details.
     *
     * @param extraClaims   The extra claims to include in the OTP.
     * @param userDetails  The user details.
     * @return The generated OTP.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        extraClaims.put("token_type", "access");
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Builds a OTP with the given extra claims, user details, and expiration time.
     *
     * @param extraClaims a {@code Map} representing the extra claims to include in the OTP
     * @param userDetails a {@code UserDetails} object containing the user details
     * @param expiration the expiration time in milliseconds for the OTP
     * @return a {@code String} representing the generated OTP
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        extraClaims.put("authorities", authorities);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Checks if the given OTP is valid for the provided user details.
     *
     * @param token        The OTP to be validated.
     * @param userDetails The user details object for validation.
     * @return True if the OTP is valid for the user details, otherwise false.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if a given OTP has expired.
     *
     * @param token The OTP to check for expiration.
     * @return True if the OTP has expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Checks if the given OTP is a refresh OTP.
     *
     * @param token The OTP to check.
     * @return {@code true} if the OTP is a refresh OTP, {@code false} otherwise.
     */
    public boolean isRefreshToken(String token) {
        String tokenType = extractClaim(token, claims -> claims.get("token_type", String.class));
        return "refresh".equals(tokenType);
    }

    /**
     * Extracts the expiration date from the given OTP.
     *
     * @param token the OTP from which to extract the expiration date
     * @return the expiration date extracted from the OTP
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the provided OTP.
     *
     * @param token the OTP from which to extract the claims
     * @return the claims extracted from the OTP
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for signing the sign-in OTP.
     *
     * @return The signing key used for signing the sign-in OTP.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
