package dev.teamproject.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling JSON Web Token (JWT) operations.
 * This class provides methods to generate JWTs for user authentication. 
 */
@Component
public class JwtUtil {
  private static final String secretKey = "Hello";

  /**
   * Generates jwt token.
   */ 
  public String generateToken(String username) {
    JwtBuilder jwt = Jwts.builder().subject(username).issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256, generateJwtSecretKey());
    return jwt.compact();
  }

  /**
   * Generates a SecretKey for JWT. 
   */ 
  public SecretKey generateJwtSecretKey() {
    // Convert the static word to a byte array
    byte[] keyBytes = secretKey.getBytes();

    // Ensure the key length is compatible with the algorithm (HMAC SHA-256 requires
    // 32 bytes)
    byte[] keyBytesPadded = new byte[32];
    System.arraycopy(keyBytes, 0, keyBytesPadded, 0, Math.min(keyBytes.length, 32));

    // Generate the SecretKey using the static word
    return Keys.hmacShaKeyFor(keyBytesPadded);
  }


  public boolean validateToken(String token, String username) {
    return username.equals(getUsername(token)) && !isTokenExpired(token);
  }  

  private boolean isTokenExpired(String token) {
    // TODO Auto-generated method stub
    return getClaims(token).getExpiration().before(new Date());
  }

  private String getUsername(String token) {
    // TODO Auto-generated method stub
    return getClaims(token).getSubject();
  }

  public String extractUsername(String token) {
    return getClaims(token).getSubject();
  }

  public Claims getClaims(String token) {
    return Jwts.parser().verifyWith(
        generateJwtSecretKey()).build().parseSignedClaims(token).getPayload();
  }
}
