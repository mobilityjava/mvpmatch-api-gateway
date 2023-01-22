package com.gateway.app.apigateway.logout.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import org.junit.jupiter.api.Test;

class InMemoryTokenBlacklistTest {

  public static final String USER = "user";
  public static final String TOKEN = "token";
  private static final String USER_2 = "user2";
  private static final String TOKEN_2 = "token2";

  private final InMemoryTokenBlacklist inMemoryTokenBlacklist = new InMemoryTokenBlacklist();

  @Test
  void blacklistShouldSucceed() {
    inMemoryTokenBlacklist.blacklist(new BlacklistEntry(USER, TOKEN, null));
    assertTrue(inMemoryTokenBlacklist.containsTokenFor(USER));
    assertTrue(inMemoryTokenBlacklist.isBlacklisted(USER, TOKEN));
  }

  @Test
  void containsTokenForShouldReturnFalseOnEmptyBlacklist() {
    assertFalse(inMemoryTokenBlacklist.containsTokenFor(USER));
  }

  @Test
  void containsTokenForShouldReturnFalseOnBlacklistWithOnlyOtherUsers() {
    inMemoryTokenBlacklist.blacklist(new BlacklistEntry(USER_2, TOKEN, null));
    assertFalse(inMemoryTokenBlacklist.containsTokenFor(USER));
  }

  @Test
  void isBlacklistedShouldReturnFalseOnEmptyBlackList(){
    assertFalse(inMemoryTokenBlacklist.isBlacklisted(USER,TOKEN));
  }

  @Test
  void isBlacklistedShouldReturnFalseOnBlackListWithOnlyOtherUsers(){
    inMemoryTokenBlacklist.blacklist(new BlacklistEntry(USER_2, TOKEN, null));
    assertFalse(inMemoryTokenBlacklist.isBlacklisted(USER,TOKEN));
  }

  @Test
  void isBlacklistedShouldReturnFalseOnBlackListWithOnlyOtherUserTokens(){
    inMemoryTokenBlacklist.blacklist(new BlacklistEntry(USER, TOKEN_2, null));
    assertFalse(inMemoryTokenBlacklist.isBlacklisted(USER,TOKEN));
  }

  @Test
  void isBlacklistedShouldReturnTrueOnBlackListsContainingTheUsersToken(){
    inMemoryTokenBlacklist.blacklist(new BlacklistEntry(USER, TOKEN, null));
    assertTrue(inMemoryTokenBlacklist.isBlacklisted(USER,TOKEN));
  }

}
