package com.easya.assetmanager.common.auth;

/**
 * Service interface for handling authorization and token management.
 * Provides methods for token validation, generation, and invalidation.
 */
public interface AuthorizationService {
  /**
   * Validates if the given token is valid and has the required permissions.
   * 
   * @param token              The authorization token to validate
   * 
   * @param requiredPermission The permission required for the operation
   * 
   * @return true if the token is valid and has the required permission
   */
  boolean validateToken(String token, String requiredPermission);

  /**
   * Generates a new authorization token for the given user.
   * 
   * @param username The username to generate token for
   * 
   * @return The generated token
   */
  String generateToken(String username);

  /**
   * Invalidates a token.
   * 
   * @param token The token to invalidate
   */
  void invalidateToken(String token);
}