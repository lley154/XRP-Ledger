package com.easya.assetmanager.keymanagement.impl;

import com.easya.assetmanager.common.config.AppConfig;
import com.easya.assetmanager.keymanagement.spi.KeyManagementService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Transaction;

/**
 * Implementation of the KeyManagementService interface for XRPL (XRP Ledger).
 * This service provides functionality for managing cryptographic keys and signing transactions
 * for the XRP Ledger blockchain.
 */
public class XrplKeyManagementService
    implements KeyManagementService<Address, PublicKey, SingleSignedTransaction<Transaction>, Transaction> {
  private static final Logger logger = LoggerFactory.getLogger(XrplKeyManagementService.class);
  private final Map<Address, KeyPair> keyStore = new ConcurrentHashMap<>();
  private static final boolean IS_MAINNET = Boolean.parseBoolean(AppConfig.getProperty("xrpl.is.mainnet"));
  private static final String TEST_PRIVATE_KEY = AppConfig.getProperty("xrpl.test.secret.key");

  /**
   * Constructs a new XrplKeyManagementService instance.
   * In non-mainnet environments, it initializes the service with a test key pair
   * loaded from the configuration.
   */
  public XrplKeyManagementService() {
    if (!IS_MAINNET) {
      // Only use test key in non-mainnet environments
      logger.warn("Using test key pair - this should only be used in development/testing environments");
      logger.debug("Loading properties from: {}", AppConfig.class.getClassLoader().getResource(TEST_PRIVATE_KEY));
      KeyPair keyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(TEST_PRIVATE_KEY)).deriveKeyPair();
      keyStore.put(keyPair.publicKey().deriveAddress(), keyPair);
    }
  }

  @Override
  public PublicKey generateKeyPair() {
    try {
      // TODO: Remove this once we have a real key pair from hardware wallet
      KeyPair keyPair = Seed.ed25519Seed().deriveKeyPair();
      Address keyAddress = keyPair.publicKey().deriveAddress();
      keyStore.put(keyAddress, keyPair);

      logger.info("Generated new Ed25519 key pair with Address: {}", keyAddress);
      return keyPair.publicKey();
    } catch (Exception e) {
      logger.error("Failed to generate key pair", e);
      throw new RuntimeException("Failed to generate key pair", e);
    }
  }

  @Override
  public PublicKey getPublicKey() {
    KeyPair keyPair = keyStore.values().iterator().next();
    if (keyPair == null) {
      throw new IllegalArgumentException("No key pair found for default publilc key");
    }
    return keyPair.publicKey();
  }

  @Override
  public PublicKey getPublicKey(Address address) {
    KeyPair keyPair = keyStore.get(address);
    if (keyPair == null) {
      throw new IllegalArgumentException("No key pair found for address: " + address);
    }
    return keyPair.publicKey();
  }

  @Override
  public SingleSignedTransaction<Transaction> signTransaction(
      Address address,
      Transaction transaction) {
    KeyPair keyPair = keyStore.get(address);
    if (keyPair == null) {
      throw new IllegalArgumentException("No key pair found for address: " + address);
    }
    try {
      // Construct a SignatureService to sign the Transaction
      SignatureService<PrivateKey> signatureService = new BcSignatureService();
      SingleSignedTransaction<Transaction> signedTransaction = signatureService.sign(keyPair.privateKey(), transaction);
      System.out.println("Signed Transaction: " + signedTransaction.signedTransaction());
      return signedTransaction;
    } catch (Exception e) {
      throw new RuntimeException("Failed to sign data", e);
    }
  }

  @Override
  public String toString() {
    return String.format("XrplKeyManagementService{keyStoreSize=%d}", keyStore.size());
  }
}