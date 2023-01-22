package com.gateway.app.apigateway.logout.control.mongo;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import com.gateway.app.apigateway.logout.control.TokenBlacklistProvider;
import java.time.Instant;

/**
 * Implementation of {@linkplain TokenBlacklistProvider} using MongoDB as backend.
 */
public class MongoTokenBlacklist implements TokenBlacklistProvider {

  private final BlacklistRepository blacklistRepository;

  public MongoTokenBlacklist(
      BlacklistRepository blacklistRepository) {
    this.blacklistRepository = blacklistRepository;
  }

  @Override
  public boolean isBlacklisted(String userId, String token) {
    return blacklistRepository.getBlacklistDocumentsByUserIdAndToken(userId, token).isPresent();
  }

  @Override
  public void blacklist(BlacklistEntry blacklistEntry) {
    var s = new BlacklistDocument(blacklistEntry.getUserId(),
        blacklistEntry.getToken(),
        blacklistEntry.getSaveUntil());

    blacklistRepository.save(s);
    // clean up old blacklist items
    blacklistRepository.deleteAllBySaveUntilBefore(Instant.now());
  }
}
