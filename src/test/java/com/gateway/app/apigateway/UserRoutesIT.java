package com.gateway.app.apigateway;


import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@ActiveProfiles("test")
@MockServerTest
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "mockserver.properties")
class UserRoutesIT {

  // will be injected by MockServer
  private MockServerClient mockServerClient;

  private WebTestClient apiGatewayClient;

  @LocalServerPort
  protected int localServerPort = 0;

  @BeforeEach
  public void setup() {
    apiGatewayClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + localServerPort)
        .build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"/v1/users","/admin/v1/users"})
  void usersPathShouldRouteToUserService(String path) {
    // given
    mockServerClient.when(request()
            .withMethod("GET")
            .withPath(path))
        .respond(response()
            .withStatusCode(200)
        );

    // when
    ResponseSpec responseSpec = apiGatewayClient.get().uri(path).exchange();

    // then
    responseSpec.expectStatus().isOk();
    mockServerClient.verify(request().withPath(path), VerificationTimes.exactly(1));
  }

  @Test
  void usersDocumentationShouldRedirectToSwagger() {
    // when
    ResponseSpec responseSpec = apiGatewayClient.get().uri("/docs/users").exchange();

    // then
    responseSpec.expectStatus().is3xxRedirection().expectHeader()
        .location("/swagger-ui/webjars/swagger-ui/index.html?url=/v3/api-docs/users");
  }

  @Test
  void usersOpenApiShouldBeAccessible() {
    // given
    mockServerClient.when(request()
            .withMethod("GET")
            .withPath("/v3/api-docs"))
        .respond(response()
            .withStatusCode(200)
        );

    // when
    ResponseSpec responseSpec = apiGatewayClient.get().uri("/v3/api-docs/users").exchange();

    // then
    responseSpec.expectStatus().isOk();
    mockServerClient.verify(request().withPath("/v3/api-docs"), VerificationTimes.exactly(1));
  }

}
