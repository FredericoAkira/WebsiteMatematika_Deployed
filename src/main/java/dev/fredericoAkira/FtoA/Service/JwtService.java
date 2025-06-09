package dev.fredericoAkira.FtoA.Service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dev.fredericoAkira.FtoA.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    /*
        JWT Token -> HEADER.PAYLOAD.SIGNATURE
     */

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    public String generateToken(User user) {
        String token = Jwts
            .builder() // mulai buat token
            .subject(user.getUsername()) // subject yang dikirim
            .claim("roles", user.getRole()) // user attribute tambahan yang mau dibawa
            .issuedAt(new Date(System.currentTimeMillis())) // bagian payload
            .expiration(new Date(System.currentTimeMillis() + 30*60*1000)) // bagian payload
            .signWith(getSigningKey()) // encryption key
            .compact();
        
        return token;

        /*
            BAGIAN PAYLOAD AKHIR
            {
                "sub": user.getUsername(),
                "roles": user.getRole(),
                "iat": issuedAt,
                "exp": expiration
            }
         */
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

        /*
            HEADER -> hmacShaKeyFor -> HS256
            SIGNATURE -> returned value
         */
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claim = extractAllClaims(token);
        return resolver.apply(claim);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isValid(String token, UserDetails user){
        String username = extractUsername(token);
        return username.equals(user.getUsername());
    }

    public boolean isExpired(String token){
        return extractExpiration(token).getTime() > new Date().getTime();
    }

}
