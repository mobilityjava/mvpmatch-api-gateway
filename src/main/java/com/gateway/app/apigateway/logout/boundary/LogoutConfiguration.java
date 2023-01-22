package com.gateway.app.apigateway.logout.boundary;

import com.gateway.app.apigateway.logout.control.mongo.BlacklistRepository;
import com.gateway.app.apigateway.logout.control.mongo.MongoTokenBlacklist;
import com.gateway.app.apigateway.logout.control.TokenBlacklistProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Defines which instance of {@linkplain TokenBlacklistProvider} is to be used.
 */
@Configuration
@Profile("!test")
public class LogoutConfiguration {

  @Bean
  public TokenBlacklistProvider blacklistProvider(BlacklistRepository blacklistRepo) {
    return new MongoTokenBlacklist(blacklistRepo);
  }


}
