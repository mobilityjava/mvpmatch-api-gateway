package com.gateway.app.apigateway.logout.control;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;

/**
 * Blacklist for invalidating tokens and persist information.
 */
public interface TokenBlacklistProvider {

  /**
   * checks whether this user token is on the blacklist.
   *
   * @param userId id of token's user
   * @param token  jwt token value
   * @return true if blacklist entry is present
   */
  boolean isBlacklisted(String userId, String token);

  /**
   * Add a token to the blacklist.
   *
   * @param blacklistEntry entry to save
   */
  void blacklist(BlacklistEntry blacklistEntry);
}
