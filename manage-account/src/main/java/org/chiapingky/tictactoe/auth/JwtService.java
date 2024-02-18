package org.chiapingky.tictactoe.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final String secretKey;

    public JwtService(String secretKey) {
        this.secretKey = secretKey;
    }

    private Key getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsernameFromToken(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public String generateToken(
            Map<String, Object> claims,
            String username
    ) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
