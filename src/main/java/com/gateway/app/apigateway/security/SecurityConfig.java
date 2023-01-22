package com.gateway.app.apigateway.security;


import static com.gateway.app.apigateway.security.ConfigConsts.ADMIN_ROLE;
import static com.gateway.app.apigateway.security.ConfigConsts.AUTHENTICATED;
import static com.gateway.app.apigateway.security.ConfigConsts.DENY_ALL;
import static com.gateway.app.apigateway.security.ConfigConsts.PERMIT_ALL;

import com.gateway.app.apigateway.security.ConfigConsts.RoleEnum;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

  @Value("${gateway.app.allowedOrigins.internalUrls:}")
  private String[] allowedInternalUrls;

  @Value("${gateway.app.allowedOrigins.externalUrls:}")
  private String[] allowedExternalUrls;

  @Order(Ordered.HIGHEST_PRECEDENCE)
  @Bean
  @ConditionalOnProperty(
      name = "gateway.app.auth",
      havingValue = "oauth2"
  )
  public SecurityWebFilterChain securityWebFilterChainOauth2(ServerHttpSecurity http) {
    http.csrf().disable();
    http
        .authorizeExchange()
        .pathMatchers(PERMIT_ALL).permitAll()
        .pathMatchers(DENY_ALL).denyAll()
        .pathMatchers(AUTHENTICATED).authenticated()
        .pathMatchers(ADMIN_ROLE).hasAuthority(RoleEnum.ADMIN.getRole())
        .anyExchange().authenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();
    return http.build();
  }

  @Order(Ordered.LOWEST_PRECEDENCE)
  @Bean
  @ConditionalOnProperty(
      name = "gateway.app.auth",
      havingValue = "none"
  )
  public SecurityWebFilterChain securityWebFilterChainNoAuth(ServerHttpSecurity http) {
    http.authorizeExchange().anyExchange().permitAll().and().csrf().disable();
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfiguration() {
    CorsConfiguration corsConfig = new CorsConfiguration();

    corsConfig.setAllowedOriginPatterns(Arrays.asList(ArrayUtils.addAll(
            allowedExternalUrls, allowedInternalUrls
        )
    ));

    corsConfig.applyPermitDefaultValues();
    corsConfig.addAllowedMethod(HttpMethod.PUT);
    corsConfig.addAllowedMethod(HttpMethod.DELETE);

    log.info("Loaded CORS methods {} for allowed origins {}",
        corsConfig.getAllowedMethods(), corsConfig.getAllowedOriginPatterns());

    UrlBasedCorsConfigurationSource source =
        new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);
    return source;
  }

}
