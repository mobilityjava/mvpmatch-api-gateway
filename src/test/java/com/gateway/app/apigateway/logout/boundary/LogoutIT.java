package com.gateway.app.apigateway.logout.boundary;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gateway.app.apigateway.logout.control.LogoutService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class LogoutIT {

  private static final String TOKEN = "some token value";

  @LocalServerPort
  protected int localServerPort = 0;

  @MockBean
  private ReactiveJwtDecoder reactiveJwtDecoder;

  @Autowired
  private LogoutService logoutService;

  private WebTestClient apiGatewayClient;

  @BeforeEach
  public void setup() {
    apiGatewayClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + localServerPort)
        .build();

  }

  @Test
  void logoutShouldReturnOkAndBlackListToken() {
    // given
    when(reactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt()));

    // when
    ResponseSpec resp = apiGatewayClient.post().uri(LogoutRouter.PATH)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN).exchange();

    // then status 200
    resp.expectStatus().isOk();
    // and user is logged out
    StepVerifier.create(logoutService.isLoggedOut(TOKEN)).expectNext(Boolean.TRUE).verifyComplete();
  }

  @Test
  void logoutShouldReturnBadRequestOnMissingToken() {
    // given
    when(reactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt()));

    // when
    ResponseSpec resp = apiGatewayClient.post().uri(LogoutRouter.PATH).exchange();

    // then
    resp.expectStatus().isBadRequest().expectBody(String.class)
        .isEqualTo(HttpHeaders.AUTHORIZATION + " header missing or no Bearer token present");
  }

  private Jwt jwt() {
    return Jwt.withTokenValue("token")
        .subject("user_id")
        .header("some", "value") // jwt header may not be empty
        .expiresAt(Instant.now().plusSeconds(10000)).build();
  }


}
