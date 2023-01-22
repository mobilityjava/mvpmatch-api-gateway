package com.gateway.app.apigateway.forwarding.boundary;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.gateway.app.apigateway.forwarding.control.ForwardingService;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.mockserver.verify.VerificationTimes;
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

@MockServerTest
@ActiveProfiles({"test", "ForwardingGatewayFilterIT"})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ForwardingGatewayFilterIT {

  // will be injected by MockServer
  private MockServerClient mockServerClient;

  public static final String PATH = "/ForwardingGatewayFilterIT";
  public static final String USER_ID = "user_id";
  public static final String EMAIL = "someemail@email.com";
  public static final String EMAIL_VERIFIED = "true";

  @LocalServerPort
  protected int localServerPort = 0;

  @MockBean
  private ReactiveJwtDecoder reactiveJwtDecoder;

  private WebTestClient apiGatewayClient;

  @BeforeEach
  public void setup() {
    apiGatewayClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + localServerPort)
        .build();

  }

  @Test
  void accessWithoutTokenShouldSucceed() {
    // given not token present
    mockServerClient.when(request()
        .withMethod("GET")
        .withPath(PATH))
        .respond(response()
            .withStatusCode(200)
        );

    // when
    ResponseSpec users = apiGatewayClient.get().uri(PATH).exchange();

    // then
    users.expectStatus().isOk();
    mockServerClient.verify(request().withPath(PATH), VerificationTimes.exactly(1));
  }


  @Test
  void accessWithTokenShouldSucceedAndCreateUserIdAndEmailAndEmailVerifiedHeader() {
    // given
    Jwt jwt = jwt();
    when(reactiveJwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));

    mockServerClient.when(request()
        .withMethod("GET")
        .withPath(PATH))
        .respond(response()
            .withStatusCode(200)
        );

    // when
    ResponseSpec exchange = apiGatewayClient.get().uri(PATH)
        // bearer header needed to trigger forwarding
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
        .exchange();
    // then
    exchange.expectStatus().isOk();
    mockServerClient
        .verify(request().withPath(PATH)
                .withHeader(ForwardingService.USER_ID_HEADER, USER_ID)
                .withHeader(ForwardingService.EMAIL_HEADER, EMAIL)
                .withHeader(ForwardingService.EMAIL_VERIFIED_HEADER, EMAIL_VERIFIED),
            VerificationTimes.exactly(1));
  }

  private Jwt jwt() {
    return Jwt.withTokenValue("token")
        .subject(USER_ID)
        .claim("email", EMAIL)
        .claim("email_verified", EMAIL_VERIFIED)
        .header("some", "value") // jwt header may not be empty
        .expiresAt(Instant.now().plusSeconds(10000)).build();
  }

}
