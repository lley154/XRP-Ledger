package com.easya.assetmanager.keymanagement.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Transaction;

class XrplKeyManagementServiceTest {

  private XrplKeyManagementService keyManagementService;

  @BeforeEach
  void setUp() {
    keyManagementService = new XrplKeyManagementService();
  }

  @Nested
  @DisplayName("Key Pair Generation Tests")
  class KeyPairGenerationTests {

    @Test
    @DisplayName("Should generate valid key pair")
    void shouldGenerateValidKeyPair() {
      PublicKey publicKey = keyManagementService.generateKeyPair();

      assertNotNull(publicKey);
    }

    @Test
    @DisplayName("Should generate different addresses")
    void shouldGenerateDifferentAddresses() {
      PublicKey publicKey1 = keyManagementService.generateKeyPair();
      PublicKey publicKey2 = keyManagementService.generateKeyPair();

      assertNotEquals(publicKey1, publicKey2);
    }
  }

  @Nested
  @DisplayName("Public Key Retrieval Tests")
  class PublicKeyRetrievalTests {

    private PublicKey publicKey;

    @BeforeEach
    void setUp() {
      publicKey = keyManagementService.generateKeyPair();
    }

    @Test
    @DisplayName("Should retrieve correct public key")
    void shouldRetrieveCorrectPublicKey() {
      PublicKey publicKey = keyManagementService.getPublicKey(this.publicKey.deriveAddress());

      assertNotNull(publicKey);
      assertEquals(this.publicKey, publicKey);
    }

    @Test
    @DisplayName("Should throw exception for invalid address")
    void shouldThrowExceptionForInvalidAddress() {
      assertThrows(IllegalArgumentException.class,
          () -> keyManagementService.getPublicKey(Address.of("invalid-address")));
    }
  }
  /*
   * @Nested
   * 
   * @DisplayName("Transaction Signing Tests")
   * class TransactionSigningTests {
   * 
   * private PublicKey defaultPublicKey;
   * private Address defaultAddress;
   * private Transaction testTransaction;
   * 
   * @BeforeEach
   * void setUp() {
   * defaultPublicKey = keyManagementService.generateKeyPair();
   * defaultAddress = defaultPublicKey.deriveAddress();
   * 
   * 
   * }
   * 
   * @Test
   * 
   * @DisplayName("Should throw exception for invalid address when signing transaction"
   * )
   * void shouldThrowExceptionForInvalidAddressWhenSigningTransaction() {
   * assertThrows(IllegalArgumentException.class, () ->
   * keyManagementService.signTransaction(publicKey.deriveAddress(),
   * testTransaction)
   * );
   * }
   * }
   * 
   */
}