package com.gateway.app.apigateway.logout.control.mongo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MongoTokenBlacklistTest {

  @Mock
  private BlacklistRepository blacklistRepository;

  private MongoTokenBlacklist mongoTokenBlacklist;

  @BeforeEach
  void setup() {
    mongoTokenBlacklist = new MongoTokenBlacklist(blacklistRepository);
  }


  @Test
  void isBlacklistedShouldQueryRepository() {
    mongoTokenBlacklist.isBlacklisted("userId", "token");

    verify(blacklistRepository, times(1)).getBlacklistDocumentsByUserIdAndToken("userId", "token");
    verifyNoMoreInteractions(blacklistRepository);
  }

  @Test
  void blacklistShouldSaveDocumentAndCleanup() {
    BlacklistEntry ble = new BlacklistEntry("userid", "token", Instant.now());
    mongoTokenBlacklist.blacklist(ble);

    verify(blacklistRepository, times(1))
        .save(new BlacklistDocument(ble.getUserId(), ble.getToken(), ble.getSaveUntil()));
    verify(blacklistRepository, times(1)).deleteAllBySaveUntilBefore(any());
    verifyNoMoreInteractions(blacklistRepository);
  }

}
