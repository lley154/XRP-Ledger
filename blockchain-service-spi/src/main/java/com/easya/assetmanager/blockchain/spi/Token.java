package com.easya.assetmanager.blockchain.spi;

import java.math.BigDecimal;

/**
 * Represents a token balance with its details.
 */
public class Token {
  private final String tokenName;
  private final String issuerAddress;
  private final BigDecimal quantity;

  /**
   * Constructs a new Token instance with the specified details.
   *
   * @param tokenName The name of the token
   * @param issuerAddress The address of the token issuer
   * @param quantity The quantity of tokens
   */
  public Token(String tokenName, String issuerAddress, BigDecimal quantity) {
    this.tokenName = tokenName;
    this.issuerAddress = issuerAddress;
    this.quantity = quantity;
  }

  public String getTokenName() {
    return tokenName;
  }

  public String getIssuerAddress() {
    return issuerAddress;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  @Override
  public String toString() {
    return String.format(
        "Token{tokenName='%s', issuerAddress=%s, quantity=%s}",
        tokenName,
        issuerAddress,
        quantity);
  }
}