package com.easya.assetmanager.blockchain.impl;

import com.easya.assetmanager.blockchain.spi.Token;
import com.easya.assetmanager.blockchain.spi.NetworkStatus;
import com.easya.assetmanager.common.auth.AuthorizationService;
import com.easya.assetmanager.common.config.AppConfig;
import com.easya.assetmanager.keymanagement.impl.XrplKeyManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.model.transactions.Address;

@TestMethodOrder(OrderAnnotation.class)
public class XrplBlockchainServiceTest {
  private static final Logger logger = LoggerFactory.getLogger(XrplBlockchainServiceTest.class);
  private static final ObjectMapper objectMapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  private static final String TESTNET_URL = AppConfig.getProperty("xrpl.testnet.url");
  private static final String TEST_AUTH_TOKEN = "test-auth-token";

  private XrplBlockchainService blockchainService;
  private XrplKeyManagementService keyManagementService;
  private AuthorizationService authorizationService;

  @BeforeEach
  void setUp() {
    // Create a simple authorization service that always returns true for testing
    authorizationService = new AuthorizationService() {
      @Override
      public boolean validateToken(String token, String permission) {
        return true;
      }

      @Override
      public String generateToken(String username) {
        return "test-token";
      }

      @Override
      public void invalidateToken(String token) {
        // No-op for testing
      }
    };

    keyManagementService = new XrplKeyManagementService();
    blockchainService = new XrplBlockchainService(
        TESTNET_URL,
        keyManagementService,
        authorizationService);
  }

  @Test
  @Order(1)
  void testGetNetworkStatus() {
    NetworkStatus status = blockchainService.getNetworkStatus(TEST_AUTH_TOKEN);

    assertNotNull(status);
    assertTrue(status.isConnected());
    assertEquals("XRPL", status.getNetworkName());
    assertNotNull(status.getNetworkVersion());
    assertTrue(status.getCurrentBlockNumber() > 0);
    assertTrue(status.getGasPrice().compareTo(BigDecimal.ZERO) > 0);
    try {
      logger.info("Network Status:\n{}", objectMapper.writeValueAsString(status));
    } catch (Exception e) {
      logger.error("Error serializing NetworkStatus", e);
    }
  }

  @Test
  @Order(2)
  void testGetBalance() {
    Address address = keyManagementService.generateKeyPair().deriveAddress();
    String txHash = blockchainService.topUp(address, new BigDecimal("42"));
    assertNotNull(txHash);
    BigDecimal balance = blockchainService.getBalance(address, TEST_AUTH_TOKEN);
    assertNotNull(balance);
    assertTrue(balance.compareTo(new BigDecimal("42")) == 0);
    logger.info("Balance: {}", balance);
  }

  @Test
  @Order(3)
  void testTransfer() {
    Address fromAddress = keyManagementService.generateKeyPair().deriveAddress();
    Address toAddress = keyManagementService.generateKeyPair().deriveAddress();

    blockchainService.topUp(fromAddress, new BigDecimal("100"));
    String txHash = blockchainService.transfer(fromAddress, toAddress, new BigDecimal("50"), TEST_AUTH_TOKEN);
    assertNotNull(txHash);
    BigDecimal balance = blockchainService.getBalance(toAddress, TEST_AUTH_TOKEN);
    assertNotNull(balance);
    assertTrue(balance.compareTo(new BigDecimal("50")) == 0);
    logger.info("Balance: {}", balance);
  }

  @Test
  @Order(4)
  void testIssueToken() {
    Address tokenHolderAddr = keyManagementService.generateKeyPair().deriveAddress();
    Address tokenIssuerAddr = keyManagementService.generateKeyPair().deriveAddress();

    blockchainService.topUp(tokenHolderAddr, new BigDecimal("100"));
    blockchainService.topUp(tokenIssuerAddr, new BigDecimal("100"));

    String txHash1 = blockchainService.createColdWalletAccount(tokenIssuerAddr, TEST_AUTH_TOKEN);
    assertNotNull(txHash1);

    String txHash2 = blockchainService.createHotWalletAccount(tokenHolderAddr, TEST_AUTH_TOKEN);
    assertNotNull(txHash2);

    String txHash3 = blockchainService.createTrustline(tokenHolderAddr, tokenIssuerAddr, "ABC", TEST_AUTH_TOKEN);
    assertNotNull(txHash3);

    String txHash4 = blockchainService.issueToken(tokenHolderAddr, tokenIssuerAddr, "ABC", BigDecimal.ONE,
        TEST_AUTH_TOKEN);
    assertNotNull(txHash4);

    List<Token> tokenBalances = blockchainService.getTokenBalances(tokenHolderAddr, TEST_AUTH_TOKEN);
    assertNotNull(tokenBalances);
    assert (tokenBalances.size() == 1);

  }

  /**
   * TODO: Implement this test
   */
  /*
   * @Test
   * 
   * @Order(3)
   * void testGetTokenBalances() {
   * List<TokenBalance> balances = blockchainService.getTokenBalances(
   * TEST_FROM_ADDRESS,
   * TEST_AUTH_TOKEN
   * );
   * 
   * assertNotNull(balances);
   * // Note: The test account might not have any token balances
   * // This test just verifies that the method executes without errors
   * }
   * 
   * @Test
   * 
   * @Order(4)
   * void testInvalidAuthToken() {
   * assertThrows(SecurityException.class, () -> {
   * blockchainService.getBalance(TEST_FROM_ADDRESS, "invalid-token");
   * });
   * }
   * 
   * @Test
   * 
   * @Order(5)
   * void testGetTransactionDetails() {
   * // Note: This test requires a valid transaction hash
   * // You would need to first perform a transaction and use its hash
   * String testTxHash = "test-hash";
   * Optional<TransactionDetails> details =
   * blockchainService.getTransactionDetails(
   * testTxHash,
   * TEST_AUTH_TOKEN
   * );
   * 
   * assertNotNull(details);
   * // The details might be empty if the transaction doesn't exist
   * }
   * 
   * @Test
   * 
   * @Order(6)
   * void testIsTransactionConfirmed() {
   * // Note: This test requires a valid transaction hash
   * String testTxHash = "test-hash";
   * boolean isConfirmed = blockchainService.isTransactionConfirmed(
   * testTxHash,
   * TEST_AUTH_TOKEN
   * );
   * 
   * // The result depends on whether the transaction exists and is confirmed
   * // This test just verifies that the method executes without errors
   * }
   * 
   * @Test
   * 
   * @Order(7)
   * void testGetTransactionHistory() {
   * assertThrows(UnsupportedOperationException.class, () -> {
   * blockchainService.getTransactionHistory(TEST_FROM_ADDRESS, TEST_AUTH_TOKEN);
   * });
   * }
   */
}