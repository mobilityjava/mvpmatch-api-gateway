package com.gateway.app.apigateway.logout.boundary;

import com.gateway.app.apigateway.common.boundary.TokenUtil;
import com.gateway.app.apigateway.logout.control.LogoutService;
import java.util.List;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global filter, that will check if a token passed as Bearer in {@linkplain
 * HttpHeaders#AUTHORIZATION} header is logged out and return a {@linkplain
 * HttpStatus#UNAUTHORIZED}.
 */
@Component
public class LogoutGatewayFilter implements GlobalFilter {

  private final LogoutService logoutService;

  public LogoutGatewayFilter(LogoutService logoutService) {
    this.logoutService = logoutService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    List<String> authHeaders = exchange.getRequest().getHeaders()
        .get(HttpHeaders.AUTHORIZATION);
    return Mono.justOrEmpty(TokenUtil.extractTokenFromAuthorizationHeaders(authHeaders))
        .flatMap(logoutService::isLoggedOut)
        .switchIfEmpty(Mono.just(Boolean.FALSE))
        .flatMap(isLoggedOut -> {
          if (Boolean.TRUE.equals(isLoggedOut)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
          } else {
            return chain.filter(exchange);
          }
        });
  }

}
