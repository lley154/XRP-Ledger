package com.easya.assetmanager.account.spi;

/**
 * Interface for passcode management operations.
 */
public interface PasscodeService {
  /**
   * Sets a new passcode.
   *
   * @param passcode The passcode to set
   * @return true if the passcode was successfully set
   */
  boolean setPasscode(String passcode);

  /**
   * Checks if a passcode is set.
   *
   * @return true if a passcode is set, false otherwise
   */
  boolean isPasscodeSet();

  /**
   * Validates if a passcode meets security requirements.
   *
   * @param passcode The passcode to validate
   * @return true if the passcode is valid
   */
  boolean isValidPasscode(String passcode);
}
