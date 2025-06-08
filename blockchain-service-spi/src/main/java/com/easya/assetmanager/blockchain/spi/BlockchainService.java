package com.easya.assetmanager.blockchain.spi;

import com.easya.assetmanager.common.auth.AuthorizationService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for blockchain operations.
 */
public interface BlockchainService<A, K> {

  /**
   * Get the KeyManagementService instance used by this implementation.
   *
   * @return The KeyManagementService instance.
   */
  K getKeyManagementService();

  /**
   * Get the AuthorizationService instance used by this implementation.
   *
   * @return The AuthorizationService instance.
   */
  AuthorizationService getAuthorizationService();

  /**
   * Get the current balance of an address.
   *
   * @param address   The blockchain address.
   * @param authToken The authorization token.
   * @return The current balance.
   */
  BigDecimal getBalance(A address, String authToken);

  /**
   * Transfer tokens from one address to another.
   *
   * @param fromAddress The source address.
   * @param toAddress   The destination address.
   * @param amount      The amount to transfer.
   * @param authToken   The authorization token.
   * @return The transaction hash if successful.
   */
  String transfer(A fromAddress, A toAddress, BigDecimal amount, String authToken);

  /**
   * Get transaction history for an address.
   *
   * @param address   The blockchain address.
   * @param authToken The authorization token.
   * @return List of transaction hashes.
   */
  List<TransactionDetails> getTransactionHistory(A address, String authToken);

  /**
   * Get transaction details by hash.
   *
   * @param transactionHash The transaction hash.
   * @param authToken       The authorization token.
   * @return Optional containing transaction details if found.
   */
  Optional<TransactionDetails> getTransactionDetails(String transactionHash, String authToken);

  /**
   * Check if a transaction is confirmed.
   *
   * @param transactionHash The transaction hash.
   * @param authToken       The authorization token.
   * @return true if the transaction is confirmed.
   */
  boolean isTransactionConfirmed(String transactionHash, String authToken);

  /**
   * Get the current network status.
   *
   * @param authToken The authorization token.
   * @return NetworkStatus object containing network information.
   */
  NetworkStatus getNetworkStatus(String authToken);

  /**
   * Gets all token balances for a given address.
   *
   * @param address   The wallet address.
   * @param authToken The authorization token.
   * @return List of token balances containing token name, issuer address, and quantity.
   */
  List<Token> getTokenBalances(A address, String authToken);

  /**
   * Configures a cold wallet account to be a token issuer.
   *
   * @param address   The wallet address.
   * @param authToken The authorization token.
   * @return The transaction hash if successful.
   */
  String createColdWalletAccount(A address, String authToken);

  /**
   * Configures a hot wallet account to manage issued assets.
   *
   * @param address   The wallet address.
   * @param authToken The authorization token.
   * @return The transaction hash if successful.
   */
  String createHotWalletAccount(A address, String authToken);

  /**
   * Creates a trustline between a token holder account and an issuer account.
   *
   * @param tokenHolderAddr The wallet address of the token holder.
   * @param tokenIssuerAddr The wallet address of the token issuer.
   * @param currencyCode    The currency symbol of the token.
   * @param authToken       The authorization token.
   * @return The transaction hash if successful.
   */
  String createTrustline(
      A tokenHolderAddr,
      A tokenIssuerAddr,
      String currencyCode,
      String authToken);

  /**
   * Issues tokens to a token holder account from a token issuer.
   *
   * @param tokenHolderAddr The wallet address of the token holder.
   * @param tokenIssuerAddr The wallet address of the token issuer.
   * @param currencyCode    The currency symbol of the token.
   * @param amount          The amount of tokens to issue.
   * @param authToken       The authorization token.
   * @return The transaction hash if successful.
   */
  String issueToken(A tokenHolderAddr, A tokenIssuerAddr, String currencyCode, BigDecimal amount, 
      String authToken);

  /**
   * Top up an address with a given amount for local testing.
   *
   * @param address The wallet address.
   * @param amount  The amount to top up.
   * @return The transaction hash if successful.
   */
  String topUp(A address, BigDecimal amount);
}