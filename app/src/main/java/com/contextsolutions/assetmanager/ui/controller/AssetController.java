package com.easya.assetmanager.ui.controller;

import com.easya.assetmanager.context.ApplicationContext;
import org.xrpl.xrpl4j.model.transactions.Address;
import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.util.Duration;
import javafx.animation.PauseTransition;


public class AssetController {

  @FXML
  private Button issueButton;

  @FXML
  private Button redeemButton;

  @FXML
  private Button freezeButton;

  @FXML
  private Button seizeButton;

  @FXML
  private TextField assetSymbolField;

  @FXML
  private TextField assetNameField;

  @FXML
  private TextField assetAmountField;

  @FXML
  private TextField assetTotalSupplyField;
  
  

  @FXML
  private PieChart liquidityPoolChart;

  public void initialize() {
    // Initialize the allocation pie chart with sample data
    PieChart.Data xrpData = new PieChart.Data("XRP", 40000.234);
    PieChart.Data rlusdData = new PieChart.Data("RLUSD", 60000.43);

    liquidityPoolChart.getData().addAll(xrpData, rlusdData);

    // Customize the pie chart appearance
    liquidityPoolChart.setLegendVisible(false);
    liquidityPoolChart.setLabelsVisible(true);
    liquidityPoolChart.setStartAngle(90); // Start from top
    liquidityPoolChart.setClockwise(true);
    liquidityPoolChart.setAnimated(true);
    liquidityPoolChart.setLegendSide(Side.RIGHT);

    // Format labels with percentages
    for (PieChart.Data data : liquidityPoolChart.getData()) {
      // double percentage = (data.getPieValue() / totalValue) * 100;
      // Format the label to show percentage
      // data.setName(String.format("%s (%.1f%%)", data.getName(), percentage));
      data.setName(String.format("%s (%.1f)", data.getName(), data.getPieValue()));
    }
  }


  private void showLoadingSpinner() {
    // Create loading spinner container
    VBox spinnerContainer = new VBox();
    spinnerContainer.setAlignment(Pos.CENTER);
    spinnerContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;");
    spinnerContainer.setPrefSize(200, 200);
    spinnerContainer.setMaxSize(200, 200);

    // Create spinner
    javafx.scene.control.ProgressIndicator spinner = new javafx.scene.control.ProgressIndicator();
    spinner.setStyle("-fx-progress-color: white;");
    spinner.setPrefSize(100, 100);
    spinner.setMaxSize(100, 100);

    // Add spinner to container
    spinnerContainer.getChildren().add(spinner);

    // Get the transfer form VBox
    Scene scene = issueButton.getScene();
    VBox transferForm = (VBox) scene.lookup("#assetForm");

    // Create a StackPane to hold the spinner
    StackPane spinnerPane = new StackPane();
    spinnerPane.setAlignment(Pos.CENTER);
    spinnerPane.setMaxWidth(Double.MAX_VALUE);
    spinnerPane.setMaxHeight(Double.MAX_VALUE);
    spinnerPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");
    spinnerPane.getChildren().add(spinnerContainer);

    // Add spinner to the transfer form
    transferForm.getChildren().add(spinnerPane);

    // Store the spinner pane for later removal
    transferForm.setUserData(spinnerPane);
  }

  private void hideLoadingSpinner() {
    Scene scene = issueButton.getScene();
    VBox transferForm = (VBox) scene.lookup("#assetForm");
    StackPane spinnerPane = (StackPane) transferForm.getUserData();
    if (spinnerPane != null) {
      transferForm.getChildren().remove(spinnerPane);
      transferForm.setUserData(null);
    }
  }

  private void showToast(String message, String transactionId) {
    // Create toast container
    VBox toastContainer = new VBox();
    toastContainer.setAlignment(Pos.CENTER);
    toastContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 5; -fx-padding: 10;");
    toastContainer.setMaxWidth(400);

    // Create message label
    Label messageLabel = new Label(message);
    messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

    // Create transaction ID label if provided
    if (transactionId != null && !transactionId.isEmpty()) {
      Label txIdLabel = new Label("Transaction ID: " + transactionId);
      txIdLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px;");
      toastContainer.getChildren().addAll(messageLabel, txIdLabel);
    } else {
      toastContainer.getChildren().add(messageLabel);
    }

    // Get the transfer form VBox
    Scene scene = issueButton.getScene();
    VBox transferForm = (VBox) scene.lookup("#assetForm");

    // Create a StackPane to hold the toast
    StackPane toastPane = new StackPane();
    toastPane.setAlignment(Pos.CENTER);
    toastPane.setMaxWidth(Double.MAX_VALUE);
    toastPane.setMaxHeight(Double.MAX_VALUE);
    toastPane.getChildren().add(toastContainer);

    // Add toast to the transfer form
    transferForm.getChildren().add(toastPane);

    // Create fade out animation
    PauseTransition delay = new PauseTransition(Duration.seconds(5));
    delay.setOnFinished(event -> {
      transferForm.getChildren().remove(toastPane);
    });
    delay.play();
  }

  private void setFormControlsEnabled(boolean enabled) {
    issueButton.setDisable(!enabled);
    redeemButton.setDisable(!enabled);
    freezeButton.setDisable(!enabled);
    seizeButton.setDisable(!enabled);
  }

  @FXML
  private void handleTokenIssue() {
    // Disable form controls and show spinner
    setFormControlsEnabled(false);
    showLoadingSpinner();
    Address tokenIssuerAddress = ApplicationContext.getInstance().getWalletService().getColdWalletAddress();
    Address tokenHolderAddress = ApplicationContext.getInstance().getWalletService().getWalletAddress();

    

    // Create a background task for the create cold wallet account
    javafx.concurrent.Task<String> createColdWalletAccountTask = new javafx.concurrent.Task<>() {
      @Override
      protected String call() throws Exception {
        // TODO: Manual topup for local testing
        ApplicationContext.getInstance().getWalletService().getBlockchainService().topUp(tokenIssuerAddress, new BigDecimal("100"));
        return ApplicationContext.getInstance().getWalletService().getBlockchainService().createColdWalletAccount(
            tokenIssuerAddress,
            ApplicationContext.getInstance().getAuthToken());
      }
    };

    // Create a background task for the create hot wallet account
    javafx.concurrent.Task<String> createHotWalletAccountTask = new javafx.concurrent.Task<>() {
      @Override
      protected String call() throws Exception {
        // TODO: Manual topup for local testing
        ApplicationContext.getInstance().getWalletService().getBlockchainService().topUp(tokenHolderAddress, new BigDecimal("100"));
        return ApplicationContext.getInstance().getWalletService().getBlockchainService().createHotWalletAccount(
            tokenHolderAddress,
            ApplicationContext.getInstance().getAuthToken());
      }
    };

    // Create a background task for the create trustline
    javafx.concurrent.Task<String> createTrustlineTask = new javafx.concurrent.Task<>() {
      @Override
      protected String call() throws Exception {
        return ApplicationContext.getInstance().getWalletService().getBlockchainService().createTrustline(
            tokenHolderAddress,
            tokenIssuerAddress,
            assetSymbolField.getText(),
            ApplicationContext.getInstance().getAuthToken());
      }
    };

    // Create a background task for the issue token
    javafx.concurrent.Task<String> issueTask = new javafx.concurrent.Task<>() {
      @Override
      protected String call() throws Exception {
        return ApplicationContext.getInstance().getWalletService().getBlockchainService().issueToken(
            tokenHolderAddress,
            tokenIssuerAddress,
            assetSymbolField.getText(),
            new BigDecimal(assetAmountField.getText()),
            ApplicationContext.getInstance().getAuthToken());
      }
    };

    // Chain the tasks together
    createColdWalletAccountTask.setOnSucceeded(event -> {
      new Thread(createHotWalletAccountTask).start();
    });

    createHotWalletAccountTask.setOnSucceeded(event -> {
      new Thread(createTrustlineTask).start();
    });

    createTrustlineTask.setOnSucceeded(event -> {
      new Thread(issueTask).start();
    });

    // Handle final task completion
    issueTask.setOnSucceeded(event -> {
      String transactionId = issueTask.getValue();
      hideLoadingSpinner();
      showToast("Issue Token submitted successfully!", transactionId);

      // Clear the form
      assetSymbolField.clear();
      assetNameField.clear();

      // Re-enable form controls
      setFormControlsEnabled(true);
    });

    // Handle task failures
    createColdWalletAccountTask.setOnFailed(event -> handleTaskFailure(createColdWalletAccountTask));
    createHotWalletAccountTask.setOnFailed(event -> handleTaskFailure(createHotWalletAccountTask));
    createTrustlineTask.setOnFailed(event -> handleTaskFailure(createTrustlineTask));
    issueTask.setOnFailed(event -> handleTaskFailure(issueTask));

    // Start the first task
    if (!ApplicationContext.getInstance().getWalletService().checkTrustline(tokenIssuerAddress)) {
      new Thread(createColdWalletAccountTask).start();
    } else {
      new Thread(createTrustlineTask).start();
      ApplicationContext.getInstance().getWalletService().storeTrustline(tokenIssuerAddress, tokenHolderAddress);
    }
  }

  private void handleTaskFailure(javafx.concurrent.Task<?> task) {
    hideLoadingSpinner();
    showToast("Error: " + task.getException().getMessage(), null);
    setFormControlsEnabled(true);
  }

}