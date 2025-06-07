package com.easya.assetmanager.wallet.impl;

import com.easya.assetmanager.blockchain.impl.XrplBlockchainService;
import com.easya.assetmanager.blockchain.spi.Token;
import com.easya.assetmanager.blockchain.spi.TransactionDetails;
import com.easya.assetmanager.common.auth.AuthorizationService;
import com.easya.assetmanager.common.config.AppConfig;
import com.easya.assetmanager.keymanagement.impl.XrplKeyManagementService;
import com.easya.assetmanager.wallet.spi.WalletService;
import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.xrpl.xrpl4j.model.transactions.Address;

/**
 * XRPL implementation of WalletService.
 */
public class XrplWalletService
    implements WalletService<Address, XrplBlockchainService, XrplKeyManagementService, TransactionDetails, Token> {
  private static final String TESTNET_URL = AppConfig.getProperty("xrpl.testnet.url");
  private Address coldWalletAddress;
  private Address hotWalletAddress;
  private Map<Address, Address> trustlines;
  private BigDecimal xrplBalance;
  private List<Token> tokenBalances;

  private final AuthorizationService authorizationService;
  private final XrplKeyManagementService keyManagementService;
  private final XrplBlockchainService blockchainService;

  /**
   * Private constructor to enforce use of factory methods.
   */
  private XrplWalletService(
      AuthorizationService authorizationService,
      XrplKeyManagementService keyManagementService,
      XrplBlockchainService blockchainService) {
    this.authorizationService = Objects.requireNonNull(authorizationService, "authorizationService must not be null");
    this.keyManagementService = Objects.requireNonNull(keyManagementService, "keyManagementService must not be null");
    this.blockchainService = Objects.requireNonNull(blockchainService, "blockchainService must not be null");
    this.hotWalletAddress = keyManagementService.generateKeyPair().deriveAddress();
    this.coldWalletAddress = keyManagementService.generateKeyPair().deriveAddress();
    this.trustlines = new HashMap<>();
  }

  /**
   * Factory method to create an XrplWalletService with default implementations.
   * 
   * @param authorizationService
   * 
   * @return A new instance of XrplWalletService with default dependencies
   */
  public static XrplWalletService createDefault(AuthorizationService authorizationService) {
    XrplKeyManagementService keyManagementService = new XrplKeyManagementService();
    XrplBlockchainService blockchainService = new XrplBlockchainService(TESTNET_URL, keyManagementService,
        authorizationService);
    return new XrplWalletService(
        authorizationService,
        keyManagementService,
        blockchainService);
  }

  /**
   * Factory method to create an XrplWalletService with custom dependencies.
   * 
   * @param authorizationService The authorization service implementation
   * @param keyManagementService The key management service implementation
   * @param blockchainService    The blockchain service implementation
   * 
   * @return A new instance of XrplWalletService with the provided dependencies
   */
  public static XrplWalletService create(
      AuthorizationService authorizationService,
      XrplKeyManagementService keyManagementService,
      XrplBlockchainService blockchainService) {
    return new XrplWalletService(authorizationService, keyManagementService, blockchainService);
  }

  @Override
  public Address getWalletAddress() {
    return hotWalletAddress;
  }

  @Override
  public Address getColdWalletAddress() {
    return coldWalletAddress;
  }

  @Override
  public AuthorizationService getAuthorizationService() {
    return authorizationService;
  }

  @Override
  public XrplKeyManagementService getKeyManagementService() {
    return keyManagementService;
  }

  @Override
  public XrplBlockchainService getBlockchainService() {
    return blockchainService;
  }

  @Override
  public void createWallet(String authToken) {
    if (!authorizationService.validateToken(authToken, "wallet.create")) {
      throw new SecurityException("Invalid or insufficient permissions");
    }
    hotWalletAddress = keyManagementService.generateKeyPair().deriveAddress();
  }

  @Override
  public BigDecimal getBalance(String authToken) {
    if (!authorizationService.validateToken(authToken, "wallet.read")) {
      throw new SecurityException("Invalid or insufficient permissions");
    }
    this.xrplBalance = blockchainService.getBalance(hotWalletAddress, authToken);
    return xrplBalance;
  }

  @Override
  public String transfer(Address toAddress, BigDecimal amount, String authToken) {
    if (!authorizationService.validateToken(authToken, "wallet.transfer")) {
      throw new SecurityException("Invalid or insufficient permissions");
    }
    return blockchainService.transfer(hotWalletAddress, toAddress, amount, authToken);
  }

  @Override
  public List<Token> getTokenBalances(String authToken) {
    if (!authorizationService.validateToken(authToken, "wallet.read")) {
      throw new SecurityException("Invalid or insufficient permissions");
    }
    this.tokenBalances = blockchainService.getTokenBalances(hotWalletAddress, authToken);
    return tokenBalances;
  }

  @Override
  public List<TransactionDetails> getTransactionHistory(String authToken) {
    if (!authorizationService.validateToken(authToken, "wallet.read")) {
      throw new SecurityException("Invalid or insufficient permissions");
    }
    return blockchainService.getTransactionHistory(hotWalletAddress, authToken);
  }

  @Override
  public void storeTrustline(Address tokenIssuer, Address tokenHolder) {
    trustlines.put(tokenIssuer, tokenHolder);
  }

  @Override
  public void removeTrustline(Address tokenIssuer) {
    trustlines.remove(tokenIssuer);
  }

  @Override
  public boolean checkTrustline(Address tokenIssuer) {
    return trustlines.get(tokenIssuer) == hotWalletAddress;
  }

  @Override
  public String toString() {
    return String.format(
        "XrplWalletService{hotWalletAddress='%s', coldWalletAddress='%s', xrplBalance=%s, tokenBalances=%s, blockchainService=%s, keyManagementService=%s}",
        hotWalletAddress,
        coldWalletAddress,
        xrplBalance,
        tokenBalances,
        blockchainService,
        keyManagementService);
  }
}