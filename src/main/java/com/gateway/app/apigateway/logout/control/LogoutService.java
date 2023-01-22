package com.gateway.app.apigateway.logout.control;

import com.gateway.app.apigateway.logout.boundary.LogoutHandler;
import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import java.time.Instant;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Provides logout functionality maintaining a blacklist via {@linkplain TokenBlacklistProvider}.
 *
 * @see LogoutHandler Logout HTTP handler
 */
@Component
@Slf4j
public class LogoutService {

  private final ReactiveJwtDecoder jwtDecoder;

  private final TokenBlacklistProvider blacklistProvider;

  public LogoutService(ReactiveJwtDecoder jwtDecoder,
      TokenBlacklistProvider blacklistProvider) {
    this.jwtDecoder = jwtDecoder;
    this.blacklistProvider = blacklistProvider;
  }

  /**
   * Logs out the given token by adding it to a blacklist.
   *
   * @param token JWT token
   */
  public void logOut(@NonNull String token) {
    var jwt = jwtDecoder.decode(token).block();
    if (jwt != null) {
      String userId = jwt.getSubject();
      Instant expiresAt = jwt.getExpiresAt();
      var blacklistEntry = new BlacklistEntry(userId, jwt.getTokenValue(), expiresAt);
      blacklistProvider.blacklist(blacklistEntry);
    }
  }

  /**
   * Checks whether a given token is on the blacklist.
   *
   * @param token JWT token
   * @return true if a matching blacklist entry was found
   */
  public Mono<Boolean> isLoggedOut(@NonNull String token) {
    return jwtDecoder.decode(token)
        // check only non expired token
        .filter(jwt -> jwt.getExpiresAt() == null || (jwt.getExpiresAt() != null && jwt.getExpiresAt().isAfter(Instant.now())))
        .map(jwt -> blacklistProvider.isBlacklisted(jwt.getSubject(), jwt.getTokenValue()))
        .switchIfEmpty(Mono.just(Boolean.FALSE));
  }


}
