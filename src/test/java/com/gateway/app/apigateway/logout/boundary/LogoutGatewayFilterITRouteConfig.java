package com.gateway.app.apigateway.logout.boundary;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Route Configuration for {@linkplain LogoutGatewayFilterIT}
 */
@Configuration
@Profile("LogoutGatewayFilterIT")
public class LogoutGatewayFilterITRouteConfig {

  // we need a route to trigger GlobalFilter
  @Bean
  public RouteLocator logoutTestRoute(RouteLocatorBuilder builder) {
    return builder.routes()
        .route(p -> p
            .path(LogoutGatewayFilterIT.PATH)
            .uri("no://op"))
        .build();
  }
}
