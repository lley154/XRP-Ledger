package com.easya.assetmanager.blockchain.spi;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Class representing blockchain transaction details.
 */
public class TransactionDetails {
  private final String hash;
  private final String fromAddress;
  private final String toAddress;
  private final BigDecimal amount;
  private final Instant timestamp;
  private final TransactionStatus status;
  private final String blockHash;
  private final long blockNumber;

  /**
   * Constructs a new TransactionDetails instance with the specified transaction information.
   *
   * @param hash The transaction hash
   * @param fromAddress The source address of the transaction
   * @param toAddress The destination address of the transaction
   * @param amount The amount transferred in the transaction
   * @param timestamp The timestamp of the transaction
   * @param status The status of the transaction
   * @param blockHash The hash of the block containing the transaction
   * @param blockNumber The number of the block containing the transaction
   */
  public TransactionDetails(String hash, String fromAddress, String toAddress, BigDecimal amount,
      Instant timestamp, TransactionStatus status, String blockHash, long blockNumber) {
    this.hash = hash;
    this.fromAddress = fromAddress;
    this.toAddress = toAddress;
    this.amount = amount;
    this.timestamp = timestamp;
    this.status = status;
    this.blockHash = blockHash;
    this.blockNumber = blockNumber;
  }

  public String getHash() {
    return hash;
  }

  public String getFromAddress() {
    return fromAddress;
  }

  public String getToAddress() {
    return toAddress;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public String getBlockHash() {
    return blockHash;
  }

  public long getBlockNumber() {
    return blockNumber;
  }
}