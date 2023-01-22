package com.gateway.app.apigateway.logout.boundary;

import com.gateway.app.apigateway.common.boundary.TokenUtil;
import com.gateway.app.apigateway.logout.control.LogoutService;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler for Logout functionality. Extracts the token value from {@linkplain
 * HttpHeaders#AUTHORIZATION} header and logs it out via {@linkplain LogoutService}
 *
 * @see LogoutRouter Logout routing configuration
 */
@Component
public class LogoutHandler {

  private final LogoutService logoutService;

  public LogoutHandler(LogoutService logoutService) {
    this.logoutService = logoutService;
  }

  /**
   * Logs out the current user by invalidating the current token.
   *
   * @param request server request with {@linkplain HttpHeaders#AUTHORIZATION} header
   * @return 400 if no bearer token found, otherwise 200
   */
  public Mono<ServerResponse> logout(ServerRequest request) {
    Optional<String> tokenFromHeader = TokenUtil
        .extractTokenFromAuthorizationHeaders(request.headers().header(HttpHeaders.AUTHORIZATION));
    if (tokenFromHeader.isEmpty()) {
      return ServerResponse.badRequest()
          .bodyValue(HttpHeaders.AUTHORIZATION + " header missing or no Bearer token present");
    }
    String token = tokenFromHeader.get();
    logoutService.logOut(token);
    return ServerResponse.ok().build();

  }


}
