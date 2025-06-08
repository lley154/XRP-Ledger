package com.easya.assetmanager.account.impl;

import com.easya.assetmanager.account.spi.PasscodeService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Default implementation of PasscodeService with basic passcode validation and secure storage.
 */
public class DefaultPasscodeService implements PasscodeService {
  private final String passcodeFile;
  private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
  private static final int IV_LENGTH = 16;
  // TODO: Fixed key for testing purposes - in production, this should be loaded from a secure
  // source
  private static final byte[] ENCRYPTION_KEY = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
      13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};

  private String currentPasscode;
  private boolean isPasscodeSet = false;

  /**
   * Creates a new DefaultPasscodeService instance. Initializes the passcode file location and
   * creates necessary directories.
   */
  public DefaultPasscodeService() {
    this.passcodeFile = System.getProperty("user.home") + "/.asset_manager/secure_passcode.dat";
    try {
      // Create the asset_manager directory if it doesn't exist
      Path assetManagerDir = Paths.get(System.getProperty("user.home"), ".asset_manager");
      System.out.println(" DefaultPasscodeService(): Asset manager directory: " + assetManagerDir);
      if (!Files.exists(assetManagerDir)) {
        Files.createDirectories(assetManagerDir);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create asset_manager directory", e);
    }
    loadPasscode();
  }

  private void loadPasscode() {
    Path passcodePath = Paths.get(passcodeFile);
    System.out.println("loadPasscode(): Passcode file: " + passcodePath);
    if (Files.exists(passcodePath)) {
      try {
        byte[] encryptedData = Files.readAllBytes(passcodePath);
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(encryptedData, 0, iv, 0, IV_LENGTH);

        SecretKey key = new SecretKeySpec(ENCRYPTION_KEY, "AES");
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] decryptedData =
            cipher.doFinal(encryptedData, IV_LENGTH, encryptedData.length - IV_LENGTH);
        currentPasscode = new String(decryptedData, StandardCharsets.UTF_8);
        isPasscodeSet = true;
      } catch (Exception e) {
        throw new RuntimeException("Failed to load passcode", e);
      }
    }
  }

  private void savePasscode(String passcode) {
    try {
      SecretKey key = new SecretKeySpec(ENCRYPTION_KEY, "AES");
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);

      // Generate IV
      byte[] iv = new byte[IV_LENGTH];
      new SecureRandom().nextBytes(iv);

      cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
      byte[] encryptedData = cipher.doFinal(passcode.getBytes(StandardCharsets.UTF_8));

      // Combine IV and encrypted data
      byte[] combined = new byte[iv.length + encryptedData.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

      System.out.println("savePasscode(): Saving passcode to: " + passcodeFile);
      Files.write(Paths.get(passcodeFile), combined);
    } catch (Exception e) {
      throw new RuntimeException("Failed to save passcode", e);
    }
  }

  @Override
  public boolean setPasscode(String passcode) {
    if (isPasscodeSet) {
      return false;
    }

    if (passcode == null || passcode.length() < 6 || !passcode.matches("\\d+")) {
      return false;
    }

    try {
      savePasscode(passcode);
      this.currentPasscode = passcode;
      this.isPasscodeSet = true;
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean isPasscodeSet() {
    return isPasscodeSet;
  }

  @Override
  public boolean isValidPasscode(String passcode) {
    return passcode != null && passcode.length() >= 6 && passcode.matches("\\d+")
        && passcode.equals(currentPasscode);
  }
}
