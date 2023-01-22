package com.gateway.app.apigateway.forwarding.boundary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Route Configuration for {@linkplain ForwardingGatewayFilterIT}
 */
@Configuration
@Profile("ForwardingGatewayFilterIT")
public class ForwardingGatewayFilterITRouteConfig {

  @Value("${mockServerPort}")
  private String mockPort;

  @Autowired
  private ForwardingGatewayFilterFactory forwardingGatewayFilterFactory;

  @Bean
  public RouteLocator forwardingTestRoute(RouteLocatorBuilder builder) {
    return builder.routes()
        .route(p -> p
            .path(ForwardingGatewayFilterIT.PATH)
            .filters(f -> f.filter(forwardingGatewayFilterFactory.apply(new Object())))
            .uri("http://localhost:"+mockPort))
        .build();
  }
}
