package dev.teamproject.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
  }

  @Test
  void testValidateToken_ValidToken() {
    String username = "testuser";
    String token = jwtUtil.generateToken(username);

    assertTrue(jwtUtil.validateToken(token, username));
  }

  @Test
  void testValidateToken_InvalidUsername() {
    String token = jwtUtil.generateToken("testuser");

    assertFalse(jwtUtil.validateToken(token, "wronguser"));
  }

  @Test
  void testExtractUsername() {
    String username = "testuser";
    String token = jwtUtil.generateToken(username);

    String extractedUsername = jwtUtil.extractUsername(token);

    assertEquals(username, extractedUsername);
  }
}