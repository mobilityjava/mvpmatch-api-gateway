package com.gateway.app.apigateway.common.boundary;

import java.util.List;
import java.util.Optional;

/**
 * Utility functions on token handling.
 */
public final class TokenUtil {

  private TokenUtil() {
  }

  /**
   * Extracts any existing Bearer token from a list of Authorization headers.
   *
   * @param authHeaders list of Authorization headers from request
   * @return token value if present
   */
  public static Optional<String> extractTokenFromAuthorizationHeaders(List<String> authHeaders) {
    if (authHeaders == null) {
      return Optional.empty();
    }
    return authHeaders.stream()
        .filter(auth -> auth.startsWith("Bearer "))
        .map(token -> token.substring(token.lastIndexOf(' ')))
        .map(String::trim)
        .findFirst();
  }

}
