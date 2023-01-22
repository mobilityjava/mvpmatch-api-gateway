package com.gateway.app.apigateway.forwarding.control;

import lombok.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Service for implementing the forwarding of token information as headers
 */
@Component
public class ForwardingService {

  public static final String USER_ID_HEADER = "X-User-Id";
  public static final String EMAIL_HEADER = "X-Email";
  public static final String EMAIL_VERIFIED_HEADER = "X-Email-Verified";
  public static final String USER_ROLE_HEADER = "X-Scope";

  /**
   * Extracts information from jwt token and prepares headers.
   *
   * @param jwt token to extract from
   * @return multi map containing header name and values
   */
  public MultiValueMap<String, String> extractHeadersFromJwt(@NonNull Jwt jwt) {
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(USER_ID_HEADER, jwt.getSubject());
    headers.add(EMAIL_HEADER, jwt.getClaim("email"));
    headers.add(EMAIL_VERIFIED_HEADER, jwt.getClaimAsString("email_verified"));
    headers.add(USER_ROLE_HEADER, jwt.getClaimAsString("scope"));
    return headers;
  }

}
