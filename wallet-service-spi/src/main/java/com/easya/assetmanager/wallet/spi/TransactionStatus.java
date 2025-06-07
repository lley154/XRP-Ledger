package com.easya.assetmanager.wallet.spi;

/**
 * Represents the status of a blockchain transaction.
 */
public enum TransactionStatus {
  /**
   * Transaction has been submitted to the network but not yet confirmed.
   */
  PENDING,

  /**
   * Transaction has been confirmed and included in a block.
   */
  CONFIRMED,

  /**
   * Transaction has failed or been rejected.
   */
  FAILED,

  /**
   * Transaction status is unknown or could not be determined.
   */
  UNKNOWN
}