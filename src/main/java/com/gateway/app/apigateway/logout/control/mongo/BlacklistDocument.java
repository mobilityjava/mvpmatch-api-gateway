package com.gateway.app.apigateway.logout.control.mongo;

import java.time.Instant;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Blacklist entry saved in mongo.
 */
@Value
@Document
public class BlacklistDocument {

  @Indexed
  String userId;
  String token;

  Instant saveUntil;

}
