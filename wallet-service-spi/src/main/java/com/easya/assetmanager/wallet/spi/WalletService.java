package com.easya.assetmanager.wallet.spi;

import com.easya.assetmanager.common.auth.AuthorizationService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for wallet operations.
 * Implementing classes must provide instances of required services
 * for blockchain operations, key management, and authorization.
 */
public interface WalletService<A, B, K, D, T> {

  /**
   * Get the wallet address.
   * 
   * @return The wallet address
   */
  A getWalletAddress();

  /**
   * Get the cold wallet address.
   * 
   * @return The cold wallet address
   */
  A getColdWalletAddress();

  /**
   * Get the AuthorizationService instance used by this implementation.
   * 
   * @return The AuthorizationService instance
   */
  AuthorizationService getAuthorizationService();

  /**
   * Get the KeyManagementService instance used by this implementation.
   * 
   * @return The KeyManagementService instance
   */
  K getKeyManagementService();

  /**
   * Get the BlockchainService instance used by this implementation.
   * 
   * @return The BlockchainService instance
   */
  B getBlockchainService();

  /**
   * Create a new wallet.
   *
   * @param authToken The authorization token
   */
  void createWallet(String authToken);

  /**
   * Get the balance of a wallet.
   * 
   * @param authToken The authorization token
   * 
   * @return The current balance
   */
  BigDecimal getBalance(String authToken);

  /**
   * Transfer funds from one wallet to another.
   *
   * @param toAddress The destination wallet address
   * @param amount    The amount to transfer
   * @param authToken The authorization token
   * 
   * @return The transaction hash if successful
   */
  String transfer(A toAddress, BigDecimal amount, String authToken);

  /**
   * Get the tokens in this wallet.
   *
   * @param authToken The authorization token
   * 
   * @return List of Tokens
   */
  List<T> getTokenBalances(String authToken);

  /**
   * Get transaction history for a wallet.
   *
   * @param authToken The authorization token
   * 
   * @return List of transaction details
   */
  List<D> getTransactionHistory(String authToken);

  /**
   * Store a trustline for a token issuer.
   *
   * @param tokenIssuer The token issuer address
   * @param tokenHolder The token holder address
   */
  void storeTrustline(A tokenIssuer, A tokenHolder);

  /**
   * Remove a trustline for a token issuer.
   *
   * @param tokenIssuer The token issuer address
   */
  void removeTrustline(A tokenIssuer);
  
  /**
   * Check if a trustline exists for a token issuer.
   *
   * @param tokenIssuer The token issuer address
   * 
   * @return true if the trustline exists, false otherwise
   */
  boolean checkTrustline(A tokenIssuer);


}