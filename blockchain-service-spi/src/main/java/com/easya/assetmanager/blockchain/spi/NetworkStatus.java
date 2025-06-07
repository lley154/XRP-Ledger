package com.easya.assetmanager.blockchain.spi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Class representing the current status of the blockchain network.
 */
public class NetworkStatus {
  @JsonProperty("currentBlockNumber")
  private final long currentBlockNumber;

  @JsonProperty("gasPrice")
  private final BigDecimal gasPrice;

  @JsonProperty("isConnected")
  private final boolean isConnected;

  @JsonProperty("networkName")
  private final String networkName;

  @JsonProperty("networkVersion")
  private final String networkVersion;

  /**
   * Constructs a new NetworkStatus instance with the specified network information.
   *
   * @param currentBlockNumber The current block number of the network
   * @param gasPrice The current gas price of the network
   * @param isConnected Whether the network is currently connected
   * @param networkName The name of the network
   * @param networkVersion The version of the network
   */
  public NetworkStatus(long currentBlockNumber, BigDecimal gasPrice, boolean isConnected,
      String networkName, String networkVersion) {
    this.currentBlockNumber = currentBlockNumber;
    this.gasPrice = gasPrice;
    this.isConnected = isConnected;
    this.networkName = networkName;
    this.networkVersion = networkVersion;
  }

  public long getCurrentBlockNumber() {
    return currentBlockNumber;
  }

  public BigDecimal getGasPrice() {
    return gasPrice;
  }

  public boolean isConnected() {
    return isConnected;
  }

  public String getNetworkName() {
    return networkName;
  }

  public String getNetworkVersion() {
    return networkVersion;
  }

  @Override
  public String toString() {
    return String.format(
        "NetworkStatus{networkName='%s', networkVersion='%s', isConnected=%b, "
            + "currentBlockNumber=%d, gasPrice=%s}",
        networkName,
        networkVersion,
        isConnected,
        currentBlockNumber,
        gasPrice);
  }
}