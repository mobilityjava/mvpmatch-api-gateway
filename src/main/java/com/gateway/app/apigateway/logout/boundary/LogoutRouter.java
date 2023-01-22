package com.gateway.app.apigateway.logout.boundary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Routing for the Logout functionality. Listens on POSTs to {@linkplain LogoutRouter#PATH} and
 * delegates to {@linkplain LogoutHandler}
 */
@Configuration
public class LogoutRouter {

  public static final String PATH = "/v1/logout";

  /**
   * Defines a route on {@linkplain LogoutRouter#PATH}.
   *
   * @param handler handler to delegate
   * @return a new routing function
   */
  @Bean
  public RouterFunction<ServerResponse> logoutRoute(LogoutHandler handler) {

    return RouterFunctions
        .route(RequestPredicates.POST(PATH),
            handler::logout);
  }
}
