package com.gateway.app.apigateway.logout.boundary;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.gateway.app.apigateway.logout.control.LogoutService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

/**
 * Test for {@linkplain LogoutGatewayFilter}. Since it's a global filter we just need a defined
 * route to trigger this filter.
 *
 * @see LogoutGatewayFilterITRouteConfig test route configuration
 */
@ActiveProfiles({"test","LogoutGatewayFilterIT"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LogoutGatewayFilterIT {

  // see test route configuration for this
  public static final String PATH = "/LogoutGatewayFilterIT";

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
  void accessWithoutTokenShouldSucceed() {
    // given - no token present

    // when
    ResponseSpec exchange = apiGatewayClient.get().uri(PATH).exchange();

    // then
    exchange.expectStatus().isOk();
  }

  @Test
  void accessWithValidTokenShouldSucceed() {
    // given
    Jwt jwt = jwt();
    when(reactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));

    // when
    ResponseSpec exchange = apiGatewayClient.get().uri(PATH)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
        .exchange();

    // then
    exchange.expectStatus().isOk();
  }

  @Test
  void accessWithLoggedOutTokenShouldReturn401() {
    // given
    Jwt jwt = jwt();
    when(reactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
    logoutService.logOut(jwt.getTokenValue());

    // when
    ResponseSpec exchange = apiGatewayClient.get().uri(PATH)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
        .exchange();

    // then
    exchange.expectStatus().isUnauthorized();
  }

  private Jwt jwt() {
    return Jwt.withTokenValue("token")
        .subject("user_id")
        .header("some", "value") // jwt header may not be empty
        .expiresAt(Instant.now().plusSeconds(10000)).build();
  }

}
