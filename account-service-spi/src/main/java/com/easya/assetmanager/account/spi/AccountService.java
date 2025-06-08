package com.easya.assetmanager.account.spi;

import com.easya.assetmanager.common.auth.AuthorizationService;

/**
 * Service interface for account management operations. Implementing classes must provide an
 * instance of AuthorizationService for token management and validation.
 */
public interface AccountService {

  /**
   * Get the AuthorizationService instance used by this implementation.
   *
   * @return The AuthorizationService instance
   */
  AuthorizationService getAuthorizationService();

  /**
   * Get the AuthorizationService instance used by this implementation.
   *
   * @return The PasscodeService instance
   */
  PasscodeService getPasscodeService();

}
