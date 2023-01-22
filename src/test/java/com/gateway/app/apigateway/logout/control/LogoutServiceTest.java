package com.gateway.app.apigateway.logout.control;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.gateway.app.apigateway.logout.entity.BlacklistEntry;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

  public static final String USER_ID = "userId";
  private LogoutService logoutService;

  @Mock
  private ReactiveJwtDecoder jwtDecoder;

  @Mock
  private TokenBlacklistProvider tokenBlackListProvider;

  @BeforeEach
  void setup() {
    logoutService = new LogoutService(jwtDecoder, tokenBlackListProvider);
  }

  @Test
  void logoutShouldBlacklistToken() {
    // given
    String token = "someToken";
    Jwt jwt = jwt(token);
    when(jwtDecoder.decode(token))
        .thenReturn(Mono.just(jwt));

    // when
    logoutService.logOut(token);

    // then
    BlacklistEntry entry = new BlacklistEntry(jwt.getSubject(), jwt.getTokenValue(),
        jwt.getExpiresAt());
    verify(tokenBlackListProvider, times(1)).blacklist(entry);
  }

  @Test
  void isLoggedOutShouldReturnFalseOnExpiredTokens() {
    String expiredToken = "expiredToken123";
    when(jwtDecoder.decode(expiredToken))
        .thenReturn(Mono.just(jwt(expiredToken, Instant.now().minusSeconds(1))));

    Mono<Boolean> loggedOut = logoutService.isLoggedOut(expiredToken);

    // then
    StepVerifier.create(loggedOut).expectNext(Boolean.FALSE).verifyComplete();

    verifyNoInteractions(tokenBlackListProvider);
  }

  @Test
  void isLoggedOutShouldReturnFalseIfNoLogoutPerformedForUser() {
    // given
    String token = "someToken";
    when(jwtDecoder.decode(token))
        .thenReturn(Mono.just(jwt(token)));

    // then
    Mono<Boolean> loggedOut = logoutService.isLoggedOut(token);
    StepVerifier.create(loggedOut).expectNext(Boolean.FALSE).verifyComplete();
  }

  @Test
  void isLoggedOutShouldReturnFalseIfUserLoggedOutWithDifferentToken() {
    // given
    String token = "someToken";
    when(jwtDecoder.decode(token))
        .thenReturn(Mono.just(jwt(token)));
    when(tokenBlackListProvider.isBlacklisted(USER_ID, token)).thenReturn(false);

    // then
    Mono<Boolean> loggedOut = logoutService.isLoggedOut(token);
    StepVerifier.create(loggedOut).expectNext(Boolean.FALSE).verifyComplete();
  }

  @Test
  void isLoggedOutShouldReturnTrueIfUserLoggedOutToken() {
    // given
    String token = "someToken";
    when(jwtDecoder.decode(token))
        .thenReturn(Mono.just(jwt(token)));
    when(tokenBlackListProvider.isBlacklisted(USER_ID, token)).thenReturn(true);

    // then
    Mono<Boolean> loggedOut = logoutService.isLoggedOut(token);
    StepVerifier.create(loggedOut).expectNext(Boolean.TRUE).verifyComplete();
  }

  @Test
  void isLoggedOutShouldReturnTrueIfUserLoggedOutTokenAndExpiredAtIsNull() {
    // given
    String token = "someToken";
    when(jwtDecoder.decode(token))
        .thenReturn(Mono.just(jwt(token, null)));
    when(tokenBlackListProvider.isBlacklisted(USER_ID, token)).thenReturn(true);

    // then
    Mono<Boolean> loggedOut = logoutService.isLoggedOut(token);
    StepVerifier.create(loggedOut).expectNext(Boolean.TRUE).verifyComplete();
  }


  private Jwt jwt(String token) {
    return jwt(token, Instant.now().plus(1, ChronoUnit.DAYS));
  }

  private Jwt jwt(String token, Instant expiresAt) {
    return Jwt.withTokenValue(token)
        .subject(USER_ID)
        .header("some", "value") // jwt header may not be empty
        .expiresAt(expiresAt).build();
  }


}
