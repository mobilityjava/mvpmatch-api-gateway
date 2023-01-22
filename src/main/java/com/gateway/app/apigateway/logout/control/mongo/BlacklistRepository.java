package com.gateway.app.apigateway.logout.control.mongo;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Access to black list entries in mongo db.
 *
 * @see MongoTokenBlacklist
 */
public interface BlacklistRepository extends MongoRepository<BlacklistDocument, String> {

  Optional<BlacklistDocument> getBlacklistDocumentsByUserIdAndToken(String userId, String token);


  void deleteAllBySaveUntilBefore(Instant time);
}
