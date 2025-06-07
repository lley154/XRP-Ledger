package com.easya.assetmanager.blockchain.impl;

import com.easya.assetmanager.blockchain.spi.BlockchainService;
import com.easya.assetmanager.blockchain.spi.NetworkStatus;
import com.easya.assetmanager.blockchain.spi.Token;
import com.easya.assetmanager.blockchain.spi.TransactionDetails;
import com.easya.assetmanager.blockchain.spi.TransactionStatus;
import com.easya.assetmanager.common.config.AppConfig;
import com.easya.assetmanager.common.auth.AuthorizationService;
import com.easya.assetmanager.keymanagement.impl.XrplKeyManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xrpl.xrpl4j.client.faucet.FaucetClient;
import org.xrpl.xrpl4j.client.faucet.FundAccountRequest;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.KeyPair;
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.accounts.AccountLinesRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.TrustLine;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.client.serverinfo.ServerInfoResult;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.transactions.AccountSet;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Hash256;
import org.xrpl.xrpl4j.model.transactions.ImmutableTrustSet;
import org.xrpl.xrpl4j.model.transactions.IssuedCurrencyAmount;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.TrustSet;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;

/**
 * Implementation of BlockchainService for XRPL (XRP Ledger).
 */
public class XrplBlockchainService implements BlockchainService<Address, XrplKeyManagementService> {
  private static final Logger logger = LoggerFactory.getLogger(XrplBlockchainService.class);

  private final XrplClient xrplClient;
  private final XrplKeyManagementService keyManagementService;
  private final AuthorizationService authorizationService;

  /**
   * Constructs a new XRPL blockchain service with the specified node URL and services.
   *
   * @param xrplNodeUrl The URL of the XRPL node to connect to
   * @param keyManagementService The key management service for handling XRPL keys
   * @param authorizationService The authorization service for validating access tokens
   */
  public XrplBlockchainService(
      String xrplNodeUrl,
      XrplKeyManagementService keyManagementService,
      AuthorizationService authorizationService) {
    this.xrplClient = new XrplClient(HttpUrl.get(xrplNodeUrl));
    this.keyManagementService = keyManagementService;
    this.authorizationService = authorizationService;
  }

  /**
   * Returns the key management service associated with this blockchain service.
   *
   * @return the XRPL key management service instance
   */
  @Override
  public XrplKeyManagementService getKeyManagementService() {
    return keyManagementService;
  }

  /**
   * Returns the authorization service associated with this blockchain service.
   *
   * @return the authorization service instance
   */
  @Override
  public AuthorizationService getAuthorizationService() {
    return authorizationService;
  }

  @Override
  public BigDecimal getBalance(Address address, String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      AccountInfoRequestParams params = AccountInfoRequestParams.builder()
          .account(address)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();

      AccountInfoResult result = xrplClient.accountInfo(params);
      return new BigDecimal(result.accountData().balance().value().toString())
          .divide(BigDecimal.valueOf(1_000_000)); // Convert drops to XRP
    } catch (JsonRpcClientErrorException e) {
      logger.error("Error fetching balance for address {}: {}", address, e.getMessage());
      throw new RuntimeException("Failed to fetch balance", e);
    }
  }

  @Override
  public String transfer(
      Address fromAddress,
      Address toAddress,
      BigDecimal amount,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.write")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Look up account address info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(fromAddress)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger accountSequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();

      Payment payment = Payment.builder()
          .account(fromAddress)
          .fee(feeResult.drops().minimumFee())
          .sequence(accountSequence)
          .destination(toAddress)
          .amount(XrpCurrencyAmount.ofXrp(amount))
          .signingPublicKey(keyManagementService.getPublicKey(fromAddress))
          .lastLedgerSequence(computeLastLedgerSequence(xrplClient))
          .build();

      System.out.println("Constructed Paymnet: " + payment);

      // Sign the transaction using KeyManagementService
      SingleSignedTransaction<Transaction> signedTransaction = 
          keyManagementService.signTransaction(fromAddress, payment);

      // Submit the signed transaction
      return submitAndWaitForValidation(signedTransaction, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error with transfer from {} to {} : {}",
          fromAddress,
          toAddress,
          e.getMessage());
      throw new RuntimeException("Failed transfer transaction", e);
    }
  }

  @Override
  public String createColdWalletAccount(
      Address accountAddress,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.write")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Look up account address info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(accountAddress)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger accountSequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();

      AccountSet setDefaultRipple = AccountSet.builder()
          .account(accountAddress)
          .fee(feeResult.drops().minimumFee())
          .sequence(accountSequence)
          .signingPublicKey(keyManagementService.getPublicKey(accountAddress))
          .setFlag(AccountSet.AccountSetFlag.DEFAULT_RIPPLE)
          .lastLedgerSequence(computeLastLedgerSequence(xrplClient))
          .build();

      System.out.println("Constructed Cold Wallet AccountSet: " + setDefaultRipple);

      // Sign the transaction using KeyManagementService
      SingleSignedTransaction<Transaction> signedTransaction = 
          keyManagementService.signTransaction(accountAddress, setDefaultRipple);

      // Submit the signed transaction
      return submitAndWaitForValidation(signedTransaction, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error creating cold wallet from {}: {}",
          accountAddress,
          e.getMessage());
      throw new RuntimeException("Failed to create cold wallet", e);
    }
  }

  @Override
  public String createHotWalletAccount(
      Address accountAddress,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.write")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Look up account address info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(accountAddress)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger accountSequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();

      AccountSet setRequireAuth = AccountSet.builder()
          .account(accountAddress)
          .fee(feeResult.drops().minimumFee())
          .sequence(accountSequence)
          .signingPublicKey(keyManagementService.getPublicKey(accountAddress))
          .setFlag(AccountSet.AccountSetFlag.REQUIRE_AUTH)
          .lastLedgerSequence(computeLastLedgerSequence(xrplClient))
          .build();

      System.out.println("Constructed Hot Wallet AccountSet: " + setRequireAuth);

      // Sign the transaction using KeyManagementService
      SingleSignedTransaction<Transaction> signedTransaction = 
          keyManagementService.signTransaction(accountAddress, setRequireAuth);

      // Submit the signed transaction
      return submitAndWaitForValidation(signedTransaction, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error creating hot wallet from {}: {}",
          accountAddress,
          e.getMessage());
      throw new RuntimeException("Failed to create hot wallet", e);
    }
  }

  @Override
  public String createTrustline(
      Address tokenHolderAddr,
      Address tokenIssuerAddr,
      String currencyCode,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.write")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Look up account address info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(tokenHolderAddr)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger accountSequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();

      ImmutableTrustSet trustSet = TrustSet.builder()
          .account(tokenHolderAddr)
          .fee(feeResult.drops().minimumFee())
          .sequence(accountSequence)
          .limitAmount(IssuedCurrencyAmount.builder()
              .currency(currencyCode)
              .issuer(tokenIssuerAddr)
              .value("10000000000") // TODO: set this in application.properties
              .build())
          .signingPublicKey(keyManagementService.getPublicKey(tokenHolderAddr))
          .build();

      System.out.println("Constructed TrustSet: " + trustSet);

      // Sign the transaction using KeyManagementService
      SingleSignedTransaction<Transaction> signedTransaction = 
          keyManagementService.signTransaction(tokenHolderAddr, trustSet);

      // Submit the signed transaction
      return submitAndWaitForValidation(signedTransaction, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error creating trustline from {} to {} : {}",
          tokenHolderAddr,
          tokenIssuerAddr,
          e.getMessage());
      throw new RuntimeException("Failed to create trustline", e);
    }
  }

  @Override
  public String issueToken(
      Address tokenHolderAddr,
      Address tokenIssuerAddr,
      String currencyCode,
      BigDecimal amount,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.write")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Look up account address info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(tokenIssuerAddr)
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger accountSequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();

      Payment payment = Payment.builder()
          .account(tokenIssuerAddr)
          .fee(feeResult.drops().minimumFee())
          .sequence(accountSequence)
          .destination(tokenHolderAddr)
          .amount(IssuedCurrencyAmount.builder()
              .issuer(tokenIssuerAddr)
              .currency(currencyCode)
              .value(amount.toString())
              .build())
          .signingPublicKey(keyManagementService.getPublicKey(tokenIssuerAddr))
          .build();

      System.out.println("Constructed Paymnet: " + payment);

      // Sign the transaction using KeyManagementService
      SingleSignedTransaction<Transaction> signedTransaction = 
          keyManagementService.signTransaction(tokenIssuerAddr, payment);

      // Submit the signed transaction
      return submitAndWaitForValidation(signedTransaction, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error issue token from {} to {} : {}",
          tokenIssuerAddr,
          tokenHolderAddr,
          e.getMessage());
      throw new RuntimeException("Failed to issue token", e);
    }
  }

  @Override
  public List<TransactionDetails> getTransactionHistory(Address address, String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Note: XRPL doesn't have a direct API for transaction history
      // This would typically be implemented using a database or external service
      throw new UnsupportedOperationException("Transaction history not implemented");
    } catch (Exception e) {
      logger.error("Error fetching transaction history for address {}: {}", address, e.getMessage());
      throw new RuntimeException("Failed to fetch transaction history", e);
    }
  }

  @Override
  public Optional<TransactionDetails> getTransactionDetails(
      String transactionHash,
      String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      TransactionRequestParams params = TransactionRequestParams.builder()
          .transaction(Hash256.of(transactionHash))
          .build();

      TransactionResult<Payment> result = xrplClient.transaction(params, Payment.class);

      // Convert XRPL transaction to our TransactionDetails format
      TransactionDetails details = new TransactionDetails(
          result.hash().value(),
          result.transaction().account().value(),
          result.transaction().destination().value(),
          new BigDecimal(result.transaction().amount().toString())
              .divide(BigDecimal.valueOf(1_000_000)), // Convert drops to XRP
          result.closeDate()
              .map(date -> Instant.ofEpochSecond(date.longValue()))
              .orElse(Instant.now()),
          TransactionStatus.valueOf(result.status().toString()),
          "", // TODO: Add block hash
          result.ledgerIndex()
              .map(LedgerIndex::unsignedIntegerValue)
              .map(UnsignedInteger::longValue)
              .orElse(0L));

      return Optional.of(details);
    } catch (JsonRpcClientErrorException e) {
      logger.error(
          "Error fetching transaction details for hash {}: {}",
          transactionHash,
          e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public boolean isTransactionConfirmed(String transactionHash, String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      TransactionRequestParams params = TransactionRequestParams.builder()
          .transaction(Hash256.of(transactionHash))
          .build();

      TransactionResult<Payment> result = xrplClient.transaction(params, Payment.class);
      return result.validated();
    } catch (JsonRpcClientErrorException e) {
      logger.error(
          "Error checking transaction confirmation for hash {}: {}", 
          transactionHash, 
          e.getMessage());
      return false;
    }
  }

  @Override
  public NetworkStatus getNetworkStatus(String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      // Get server info to determine network status
      ServerInfoResult serverInfo = xrplClient.serverInformation();

      // Get the latest validated ledger index
      LedgerIndex validatedLedger = xrplClient.ledger(
          LedgerRequestParams.builder()
              .ledgerSpecifier(LedgerSpecifier.VALIDATED)
              .build())
          .ledgerIndex()
          .orElseThrow(() -> new RuntimeException("LedgerIndex not available."));

      return new NetworkStatus(
          validatedLedger.unsignedIntegerValue().longValue(),
          new BigDecimal(xrplClient.fee().drops().openLedgerFee().value().toString()),
          true, // isConnected
          "XRPL", // networkName
          serverInfo.info().networkId().toString() // networkVersion
      );
    } catch (JsonRpcClientErrorException e) {
      logger.error("Error fetching network status: {}", e.getMessage());
      throw new RuntimeException("Failed to fetch network status", e);
    }
  }

  @Override
  public List<Token> getTokenBalances(Address address, String authToken) {
    if (!authorizationService.validateToken(authToken, "blockchain.read")) {
      throw new SecurityException("Invalid authorization token");
    }

    try {
      List<Token> balances = new ArrayList<>();

      // Get account lines (token balances)
      List<TrustLine> lines = xrplClient.accountLines(
          AccountLinesRequestParams.builder()
              .account(address)
              .ledgerSpecifier(LedgerSpecifier.VALIDATED)
              .build())
          .lines();

      // Process each token balance
      lines.forEach(line -> {
        String tokenName = line.currency();
        String issuerAddress = line.account().value();
        BigDecimal quantity = new BigDecimal(line.balance().toString());

        // Only include non-zero balances
        if (quantity.compareTo(BigDecimal.ZERO) != 0) {
          balances.add(new Token(tokenName, issuerAddress, quantity));
        }
      });

      System.out.println("Token Balances: " + balances.toString());

      return balances;
    } catch (JsonRpcClientErrorException e) {
      logger.error(
          "Error fetching token balances for address {}: {}", 
          address, 
          e.getMessage());
      throw new RuntimeException("Failed to fetch token balances", e);
    }
  }

  @Override
  public String topUp(Address address, BigDecimal amount) {
    if (Boolean.parseBoolean(AppConfig.getProperty("xrpl.is.testnet"))) {
      FaucetClient faucetClient = FaucetClient.construct(HttpUrl.get("https://faucet.altnet.rippletest.net"));
      System.out.println("Topped up address: " + address);
      return faucetClient.fundAccount(FundAccountRequest.of(address)).toString();
    } else {
    try {
      String gensisAddress = "rHb9CJAWyB4rj91VRWn96DkukG4bwdtyTh";
      String gensisPrivateKey = "snoPBrXtMeMyMHUVTgbuqAfg1SUTb";
      KeyPair gensisWalletKeyPair = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(gensisPrivateKey))
          .deriveKeyPair();

      // Look up your Account Info
      AccountInfoRequestParams requestParams = AccountInfoRequestParams.builder()
          .account(Address.of(gensisAddress))
          .ledgerSpecifier(LedgerSpecifier.VALIDATED)
          .build();
      AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
      UnsignedInteger sequence = accountInfoResult.accountData().sequence();

      // Request current fee information from rippled
      FeeResult feeResult = xrplClient.fee();
      XrpCurrencyAmount openLedgerFee = feeResult.drops().openLedgerFee();

      // Get the latest validated ledger index
      LedgerIndex validatedLedger = xrplClient.ledger(
          LedgerRequestParams.builder()
              .ledgerSpecifier(LedgerSpecifier.VALIDATED)
              .build())
          .ledgerIndex()
          .orElseThrow(() -> new RuntimeException("LedgerIndex not available."));

      // LastLedgerSequence is the current ledger index + 4
      UnsignedInteger lastLedgerSequence = validatedLedger.plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue();

      // Construct a Payment
      Payment payment = Payment.builder()
          .account(Address.of(gensisAddress))
          .amount(XrpCurrencyAmount.ofXrp(amount))
          .destination(address)
          .sequence(sequence)
          .fee(openLedgerFee)
          .signingPublicKey(gensisWalletKeyPair.publicKey())
          .lastLedgerSequence(lastLedgerSequence)
          .build();
      System.out.println("Constructed Payment: " + payment);

      // Construct a SignatureService to sign the Payment Transaction
      SignatureService<PrivateKey> signatureService = new BcSignatureService();
      SingleSignedTransaction<Payment> signedPayment = signatureService.sign(gensisWalletKeyPair.privateKey(), payment);
      System.out.println("Signed Payment: " + signedPayment.signedTransaction());

      // Submit the Payment
      return submitAndWaitForValidation(signedPayment, xrplClient);

    } catch (Exception e) {
      logger.error(
          "Error creating topup to {}: {}", 
          address, 
          e.getMessage());
      throw new RuntimeException("Failed to create topup", e);
    }
  }
  }

  @Override
  public String toString() {
    return String.format("XrplBlockchainService{keyManagementService=%s}",
        keyManagementService);
  }

  // Helper methods ------------------------------------------------------------
  private static String submitAndWaitForValidation(
      SingleSignedTransaction<?> signedTransaction,
      XrplClient xrplClient)
      throws InterruptedException, JsonRpcClientErrorException, JsonProcessingException {

    SubmitResult<?> result = xrplClient.submit(signedTransaction);

    boolean transactionValidated = false;
    boolean transactionExpired = false;
    while (!transactionValidated && !transactionExpired) {
      Thread.sleep(1000);
      LedgerIndex latestValidatedLedgerIndex = xrplClient.ledger(
          LedgerRequestParams.builder()
              .ledgerSpecifier(LedgerSpecifier.VALIDATED)
              .build())
          .ledgerIndex()
          .orElseThrow(() -> new RuntimeException("Ledger response did not contain a LedgerIndex."));

      TransactionResult<Payment> transactionResult = xrplClient.transaction(
          TransactionRequestParams.of(signedTransaction.hash()),
          Payment.class);

      if (transactionResult.validated()) {
        System.out.println(
            "Transaction was validated with result code " 
            + transactionResult.metadata().get().transactionResult());
        transactionValidated = true;
      } else {
        boolean lastLedgerSequenceHasPassed = signedTransaction
            .signedTransaction()
            .lastLedgerSequence()
            .<Boolean>map((signedTransactionLastLedgerSeq) -> latestValidatedLedgerIndex
                .unsignedIntegerValue()
                .compareTo(signedTransactionLastLedgerSeq) > 0)
            .orElse(false);

        if (lastLedgerSequenceHasPassed) {
          System.out.println(
              "LastLedgerSequence has passed. Last tx response: " 
              + transactionResult);
          transactionExpired = true;
        } else {
          System.out.println("Transaction not yet validated.");
        }
      }
    }
    System.out.println("Transaction result: " 
        + result.transactionResult().hash().toString());
    return result.transactionResult().hash().toString();
  }

  // Helper methods ------------------------------------------------------------
  private static UnsignedInteger computeLastLedgerSequence(XrplClient xrplClient)
      throws JsonRpcClientErrorException {
    // Get the latest validated ledger index
    LedgerIndex validatedLedger = xrplClient.ledger(
        LedgerRequestParams.builder()
            .ledgerSpecifier(LedgerSpecifier.VALIDATED)
            .build())
        .ledgerIndex()
        .orElseThrow(() -> new RuntimeException("LedgerIndex not available."));

    return validatedLedger.plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue();
  }
}