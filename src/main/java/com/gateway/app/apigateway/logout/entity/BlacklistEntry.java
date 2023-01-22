package com.gateway.app.apigateway.logout.entity;

import com.gateway.app.apigateway.logout.control.TokenBlacklistProvider;
import java.time.Instant;
import lombok.Value;

/**
 * Entry on {@linkplain TokenBlacklistProvider}
 */
@Value
public class BlacklistEntry {

  String userId;
  String token;
  Instant saveUntil;
}
