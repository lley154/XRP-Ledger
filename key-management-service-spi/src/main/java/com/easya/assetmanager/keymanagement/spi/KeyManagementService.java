package com.easya.assetmanager.keymanagement.spi;

/**
 * Service interface for managing cryptographic keys and performing
 * cryptographic operations.
 */
public interface KeyManagementService<A, P, S, T> {

  /**
   * Generates a new key pair (public and private key).
   *
   * @return A public key of the new key pair
   */
  P generateKeyPair();

  /**
   * Retrieves the default public key of a wallet.
   *
   * @return The public key associated with the address
   * @throws IllegalArgumentException if the address is invalid or the key doesn't
   *                                  exist
   */
  P getPublicKey();

  /**
   * Retrieves the public key for a given address if it is present in the key
   * store.
   *
   * @param address The address of the key
   * @return The public key associated with the address
   * @throws IllegalArgumentException if the address is invalid or the key doesn't
   *                                  exist
   */
  P getPublicKey(A address);

  /**
   * Signs a transaction using the private key associated with the given address.
   *
   * @param address     The address of the key to use for signing
   * @param transaction The transaction data to sign
   * @return The signed transaction
   * @throws SignatureException       if signing fails
   * @throws IllegalArgumentException if the address is invalid
   */
  S signTransaction(A address, T transaction);
}