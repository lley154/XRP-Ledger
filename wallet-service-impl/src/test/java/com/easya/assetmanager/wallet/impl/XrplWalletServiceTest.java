package com.easya.assetmanager.wallet.impl;

import com.easya.assetmanager.blockchain.impl.XrplBlockchainService;
import com.easya.assetmanager.common.auth.AuthorizationService;
import com.easya.assetmanager.keymanagement.impl.XrplKeyManagementService;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.model.transactions.Address;

public class XrplWalletServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(XrplWalletServiceTest.class);
  private XrplWalletService walletService;
  private TestAuthorizationService authService;
  private XrplKeyManagementService keyManagementService;
  private XrplBlockchainService blockchainService;

  private static final String VALID_AUTH_TOKEN = "valid-token";
  private static final String INVALID_AUTH_TOKEN = "invalid-token";

  @BeforeEach
  public void setUp() {
    authService = new TestAuthorizationService();
    keyManagementService = new XrplKeyManagementService();
    blockchainService = new XrplBlockchainService("http://localhost:5006", keyManagementService, authService);
    walletService = XrplWalletService.create(authService, keyManagementService, blockchainService);
  }

  @Test
  @Order(1)
  public void testCreateWallet() {
    // Test with valid token
    walletService.createWallet(VALID_AUTH_TOKEN);
    assertNotNull(walletService.getKeyManagementService());

    // Test with invalid token
    SecurityException exception = assertThrows(SecurityException.class, () -> {
      walletService.createWallet(INVALID_AUTH_TOKEN);
    });
    assertEquals("Invalid or insufficient permissions", exception.getMessage());
  }

  @Test
  @Order(2)
  public void testGetBalance() {
    // Create wallet first
    walletService.createWallet(VALID_AUTH_TOKEN);
    walletService.getBlockchainService().topUp(walletService.getWalletAddress(), new BigDecimal("100.0"));

    // Test with valid token
    BigDecimal balance = walletService.getBalance(VALID_AUTH_TOKEN);
    assertEquals(new BigDecimal("100.0").setScale(1), balance.setScale(1));

    // Test with invalid token
    SecurityException exception = assertThrows(SecurityException.class, () -> {
      walletService.getBalance(INVALID_AUTH_TOKEN);
    });
    assertEquals("Invalid or insufficient permissions", exception.getMessage());
    logger.info("getBalance(): {}", walletService.toString());

  }

  @Test
  @Order(3)
  public void testTransfer() {
    // Create wallet first
    walletService.createWallet(VALID_AUTH_TOKEN);
    walletService.getBlockchainService().topUp(walletService.getWalletAddress(), new BigDecimal("100.0"));
    logger.info("testTransfer(): Created walletService: {}", walletService.toString());

    Address toAddress = walletService.getBlockchainService().getKeyManagementService().generateKeyPair()
        .deriveAddress();
    BigDecimal amount = new BigDecimal("10.0");

    // Test with valid token
    String txHash = walletService.transfer(toAddress, amount, VALID_AUTH_TOKEN);
    assertNotNull(txHash);

    BigDecimal balance = walletService.getBlockchainService().getBalance(toAddress, VALID_AUTH_TOKEN);
    assertEquals(new BigDecimal("10.0").setScale(1), balance.setScale(1));

    // Test with invalid token
    SecurityException exception = assertThrows(SecurityException.class, () -> {
      walletService.transfer(toAddress, amount, INVALID_AUTH_TOKEN);
    });
    assertEquals("Invalid or insufficient permissions", exception.getMessage());
  }
  /*
   * @Test
   * public void testGetTransactionHistory() {
   * // Create wallet first
   * walletService.createWallet(VALID_AUTH_TOKEN);
   * 
   * // Test with valid token
   * List<String> history = walletService.getTransactionHistory(VALID_AUTH_TOKEN);
   * assertNotNull(history);
   * 
   * // Test with invalid token
   * SecurityException exception = assertThrows(SecurityException.class, () -> {
   * walletService.getTransactionHistory(INVALID_AUTH_TOKEN);
   * });
   * assertEquals("Invalid or insufficient permissions", exception.getMessage());
   * }
   */

  @Test
  @Order(4)
  public void testCreateDefault() {
    XrplWalletService defaultService = XrplWalletService.createDefault(authService);
    assertNotNull(defaultService);
    assertNotNull(defaultService.getAuthorizationService());
    assertNotNull(defaultService.getKeyManagementService());
    assertNotNull(defaultService.getBlockchainService());
  }

  // Test implementation of AuthorizationService
  private static class TestAuthorizationService implements AuthorizationService {
    @Override
    public boolean validateToken(String token, String permission) {
      return VALID_AUTH_TOKEN.equals(token);
    }

    @Override
    public String generateToken(String username) {
      return VALID_AUTH_TOKEN;
    }

    @Override
    public void invalidateToken(String token) {
      // No-op for testing
    }
  }
}