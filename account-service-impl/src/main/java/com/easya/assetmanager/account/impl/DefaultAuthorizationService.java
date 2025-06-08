package com.easya.assetmanager.account.impl;

import com.easya.assetmanager.common.auth.AuthorizationService;
import java.util.UUID;

/**
 * Default implementation of AuthorizationService with basic token management.
 */
public class DefaultAuthorizationService implements AuthorizationService {
  @Override
  public boolean validateToken(String token, String requiredPermission) {
    return token != null && !token.isEmpty();
  }

  @Override
  public String generateToken(String username) {
    return UUID.randomUUID().toString();
  }

  @Override
  public void invalidateToken(String token) {
    // Default implementation does nothing
  }
}
