package com.gateway.app.apigateway;

import com.gateway.app.apigateway.logout.control.InMemoryTokenBlacklist;
import com.gateway.app.apigateway.logout.control.TokenBlacklistProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayTestConfiguration {

  @Bean
  public TokenBlacklistProvider blacklistProvider() {
    return new InMemoryTokenBlacklist();
  }

}
