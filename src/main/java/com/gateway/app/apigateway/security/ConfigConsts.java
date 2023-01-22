package com.gateway.app.apigateway.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for the handling Security configuration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigConsts {

  /**
   * Permit All.
   */
  static final String[] PERMIT_ALL = {
      "/login.html", "/register.html", "/js/*", "/css/*", "/favicon.ico",
      // Documentation
      "", "index.html", "/swagger-ui/**", "/docs/**", "/v3/api-docs/**"
  };

  /**
   * Deny All.
   */
  static final String[] DENY_ALL = {"/internal/**"};

  /**
   * Authenticated.
   */
  static final String[] AUTHENTICATED = {"/v1/users/**", "/v1/product/**"};

  /**
   * Admin Authority.
   */
  static final String[] ADMIN_ROLE = {"/admin/**", "/v1/admin/**"};



  public enum RoleEnum {
    ADMIN("SCOPE_admin");

    private String role;

    RoleEnum(String userRole) { this.role = userRole; }

    public String getRole() { return role; }
  }

}
