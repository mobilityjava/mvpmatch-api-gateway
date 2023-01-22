package com.gateway.app.apigateway.forwarding.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class ForwardingServiceTest {

  public static final String USER_ID = "userId";
  public static final String EMAIL = "someemail@email.com";
  public static final String EMAIL_VERIFIED = "true";
  private ForwardingService forwardingService;

  @BeforeEach
  void setup() {
    forwardingService = new ForwardingService();
  }

  @Test
  void extractHeadersFromJwtShouldFailOnJwtIsNull() {
    NullPointerException npe = assertThrows(NullPointerException.class, () -> {
      forwardingService.extractHeadersFromJwt(null);
    });
    assertEquals("jwt is marked non-null but is null",npe.getMessage());
  }

  @Test
  void extractHeadersFromJwtShouldExtractUserId() {
    Jwt jwt = jwt();
    var extractedHeaders = forwardingService.extractHeadersFromJwt(jwt);
    assertNotNull(extractedHeaders);
    assertEquals(3, extractedHeaders.size());
    assertEquals(USER_ID, extractedHeaders.getFirst(ForwardingService.USER_ID_HEADER));
  }

  @Test
  void extractHeadersFromJwtShouldExtractEmail() {
    Jwt jwt = jwt();
    var extractedHeaders = forwardingService.extractHeadersFromJwt(jwt);
    assertNotNull(extractedHeaders);
    assertEquals(3, extractedHeaders.size());
    assertEquals(EMAIL, extractedHeaders.getFirst(ForwardingService.EMAIL_HEADER));
  }

  @Test
  void extractHeadersFromJwtShouldExtractEmailVerified() {
    Jwt jwt = jwt();
    var extractedHeaders = forwardingService.extractHeadersFromJwt(jwt);
    assertNotNull(extractedHeaders);
    assertEquals(3, extractedHeaders.size());
    assertEquals(EMAIL_VERIFIED, extractedHeaders.getFirst(ForwardingService.EMAIL_VERIFIED_HEADER));
  }

  private static Jwt jwt() {
    return Jwt
        .withTokenValue("tokenValue")
        .subject(USER_ID)
        .claim("email", EMAIL)
        .claim("email_verified", EMAIL_VERIFIED)
        .header("some", "header") // header may not be empty
        .build();
  }

}
