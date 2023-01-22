package com.gateway.app.apigateway.common.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TokenUtilTest {

  @Test
  void extractTokenShouldReturnEmptyOnNull() {
    assertTrue(TokenUtil.extractTokenFromAuthorizationHeaders(null).isEmpty());
  }

  @Test
  void extractTokenShouldReturnEmptyOnEmptyList() {
    assertTrue(TokenUtil.extractTokenFromAuthorizationHeaders(List.of()).isEmpty());
  }

  @Test
  void extractTokenShouldReturnEmptyOnListWithoutBearer() {
    assertTrue(TokenUtil.extractTokenFromAuthorizationHeaders(List.of("basic auth credentials"))
        .isEmpty());
  }

  @Test
  void extractTokenShouldReturnTokenOnListWithBearer() {
    Optional<String> bearerTokenValue = TokenUtil
        .extractTokenFromAuthorizationHeaders(
            List.of("basic auth credentials", "Bearer tokenValue"));
    assertTrue(bearerTokenValue.isPresent());
    assertEquals("tokenValue", bearerTokenValue.get());
  }


}
