package com.easya.assetmanager.context;

import com.easya.assetmanager.account.impl.DefaultAccountService;
import com.easya.assetmanager.wallet.impl.XrplWalletService;

/**
 * Application context is used to access application components from the UI
 */
public class ApplicationContext {
  private static ApplicationContext instance;
  private final XrplWalletService walletService;
  private final DefaultAccountService accountService;
  private String authToken;

  private ApplicationContext() {
    accountService = DefaultAccountService.createDefault();
    if (!accountService.getPasscodeService().isPasscodeSet()) {
      accountService.getPasscodeService().setPasscode("123456");
    }
    walletService = XrplWalletService.createDefault(accountService.getAuthorizationService());
    authToken = accountService.getAuthorizationService().generateToken("testuser");
  }

  /**
   * Get an instance of the application context
   * 
   * @return
   */
  public static synchronized ApplicationContext getInstance() {
    if (instance == null) {
      instance = new ApplicationContext();
    }
    return instance;
  }

  public XrplWalletService getWalletService() {
    return walletService;
  }

  public DefaultAccountService getAccountService() {
    return accountService;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void invalidateAuthToken() {
    accountService.getAuthorizationService().invalidateToken(authToken);
  }

  public void refreshAuthToken() {
    authToken = accountService.getAuthorizationService().generateToken("testuser");
  }

}