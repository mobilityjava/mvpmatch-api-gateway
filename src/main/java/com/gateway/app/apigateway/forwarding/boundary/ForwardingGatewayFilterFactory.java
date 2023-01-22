package com.gateway.app.apigateway.forwarding.boundary;

import com.gateway.app.apigateway.common.boundary.TokenUtil;
import com.gateway.app.apigateway.forwarding.control.ForwardingService;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gatewayfilter for forwarding information from JWTs to downstream services as headers
 */
@Component
public class ForwardingGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

  private final ReactiveJwtDecoder jwtDecoder;
  private final ForwardingService forwardingService;

  public ForwardingGatewayFilterFactory(
      ReactiveJwtDecoder jwtDecoder,
      ForwardingService forwardingService) {
    this.jwtDecoder = jwtDecoder;
    this.forwardingService = forwardingService;
  }

  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
      return Mono.justOrEmpty(TokenUtil.extractTokenFromAuthorizationHeaders(authHeaders))
          .flatMap(jwtDecoder::decode)
          .map(forwardingService::extractHeadersFromJwt)
          .map(headers -> addHeaderToRequest(exchange, headers))
          .flatMap(chain::filter).switchIfEmpty(chain.filter(exchange));
    };
  }

  private ServerWebExchange addHeaderToRequest(ServerWebExchange exchange,
      MultiValueMap<String, String> headers) {
    var requestBuilder = exchange.getRequest().mutate();
    for (Entry<String, List<String>> entry : headers.entrySet()) {
      String[] values = entry.getValue().toArray(new String[0]);
      requestBuilder.header(entry.getKey(), values);
    }
    requestBuilder.build();
    return exchange;
  }

}
