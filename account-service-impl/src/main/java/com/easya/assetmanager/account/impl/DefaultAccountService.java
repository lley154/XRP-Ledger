package com.easya.assetmanager.account.impl;

import com.easya.assetmanager.account.spi.AccountService;
import com.easya.assetmanager.account.spi.PasscodeService;
import com.easya.assetmanager.common.auth.AuthorizationService;

/**
 * Default implementation of the AccountService interface. Provides account management functionality
 * with configurable dependencies.
 */
public class DefaultAccountService implements AccountService {

  private final PasscodeService passcodeService;
  private final AuthorizationService authorizationService;

  /**
   * Private constructor to enforce use of factory method.
   */
  private DefaultAccountService(PasscodeService passcodeService,
      AuthorizationService authorizationService) {
    this.passcodeService = passcodeService;
    this.authorizationService = authorizationService;
  }

  /**
   * Factory method to create an AccountServiceImpl with default implementations.
   *
   * @return A new instance of AccountServiceImpl with default dependencies.
   */
  public static DefaultAccountService createDefault() {
    return new DefaultAccountService(new DefaultPasscodeService(),
        new DefaultAuthorizationService());
  }

  /**
   * Factory method to create an AccountServiceImpl with custom dependencies.
   *
   * @param passcodeService The passcode service implementation.
   * @param authorizationService The authorization service implementation.
   *
   * @return A new instance of AccountServiceImpl with the provided dependencies.
   */
  public static DefaultAccountService create(PasscodeService passcodeService,
      AuthorizationService authorizationService) {
    return new DefaultAccountService(passcodeService, authorizationService);
  }

  /**
   * Get the AuthorizationService instance used by this implementation.
   *
   * @return The AuthorizationService instance.
   */
  @Override
  public AuthorizationService getAuthorizationService() {
    return authorizationService;
  }

  /**
   * Get the PasscodeService instance used by this implementation.
   *
   * @return The PasscodeService instance.
   */
  @Override
  public PasscodeService getPasscodeService() {
    return passcodeService;
  }
}
