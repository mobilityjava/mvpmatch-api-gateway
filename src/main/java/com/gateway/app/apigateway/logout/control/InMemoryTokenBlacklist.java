package com.gateway.app.apigateway.logout.control;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import java.util.List;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * <p>!! Warning: Not for production use. !!</p>
 * <p>No multiple instances supported No cleanup of expired tokens</p>
 */
public class InMemoryTokenBlacklist implements TokenBlacklistProvider {

  private final MultiValueMap<String, BlacklistEntry> tokenBlacklist = new LinkedMultiValueMap<>();

  @Override
  public boolean isBlacklisted(String userId, String token) {
    List<BlacklistEntry> blacklistEntries = tokenBlacklist.get(userId);
    if (blacklistEntries == null || blacklistEntries.isEmpty()) {
      return false;
    }
    return blacklistEntries.stream()
        .map(BlacklistEntry::getToken)
        .anyMatch(loggedOutToken -> loggedOutToken.equalsIgnoreCase(token));
  }

  public boolean containsTokenFor(String userId) {
    return tokenBlacklist.containsKey(userId);
  }

  @Override
  public void blacklist(BlacklistEntry blacklistEntry) {
    tokenBlacklist.add(blacklistEntry.getUserId(), blacklistEntry);
  }


}
