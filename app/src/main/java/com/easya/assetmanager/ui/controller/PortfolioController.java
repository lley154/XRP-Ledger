package com.easya.assetmanager.ui.controller;

import com.easya.assetmanager.context.ApplicationContext;
import com.easya.assetmanager.blockchain.spi.Token;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.xrpl.xrpl4j.model.transactions.Address;
import java.util.List;

public class PortfolioController {

  @FXML
  private Button themeToggleBtn;

  @FXML
  private LineChart<String, Number> portfolioChart;

  @FXML
  private PieChart allocationChart;

  @FXML
  private TableView<Holding> holdingsTable;

  @FXML
  private TableColumn<Holding, String> issuerColumn;

  @FXML
  private TableColumn<Holding, String> symbolColumn;

  @FXML
  private TableColumn<Holding, Double> quantityColumn;

  @FXML
  private Label fromAddressLabel;

  @FXML
  private TextField toAddressTextField;

  @FXML
  private TextField quantityTextField;

  @FXML
  private ComboBox<String> issuerComboBox;

  @FXML
  private ComboBox<String> symbolComboBox;

  @FXML
  private Button sendButton;

  private ObservableList<Holding> holdingsData = FXCollections.observableArrayList();

  // Data model class for holdings
  public static class Holding {
    private final SimpleStringProperty issuer;
    private final SimpleStringProperty symbol;
    private final SimpleDoubleProperty quantity;

    public Holding(String issuer, String symbol, double quantity) {
      this.issuer = new SimpleStringProperty(issuer);
      this.symbol = new SimpleStringProperty(symbol);
      this.quantity = new SimpleDoubleProperty(quantity);
    }

    public String getIssuer() {
      return issuer.get();
    }

    public void setIssuer(String value) {
      issuer.set(value);
    }

    public SimpleStringProperty issuerProperty() {
      return issuer;
    }

    public String getSymbol() {
      return symbol.get();
    }

    public void setSymbol(String value) {
      symbol.set(value);
    }

    public SimpleStringProperty symbolProperty() {
      return symbol;
    }

    public double getQuantity() {
      return quantity.get();
    }

    public void setQuantity(double value) {
      quantity.set(value);
    }

    public SimpleDoubleProperty quantityProperty() {
      return quantity;
    }
  }

  @FXML
  public void initialize() {
    // Set wallet address
    fromAddressLabel.setText(ApplicationContext.getInstance().getWalletService().getWalletAddress().toString());

    // Initialize holdings table
    symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());
    issuerColumn.setCellValueFactory(cellData -> cellData.getValue().issuerProperty());
    quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

    // Fetch actual trustlines from XRPL ledger
    try {
      List<Token> tokenBalances = ApplicationContext.getInstance().getWalletService()
          .getTokenBalances(ApplicationContext.getInstance().getAuthToken());
      
      // Clear existing data
      holdingsData.clear();
      
      // Add actual trustline data
      for (Token token : tokenBalances) {
        holdingsData.add(new Holding(
            token.getIssuerAddress(),
            token.getTokenName(),
            token.getQuantity().doubleValue()
        ));
      }
    } catch (Exception e) {
      // Log error and show empty table
      System.err.println("Error fetching trustlines: " + e.getMessage());
      holdingsData.clear();
    }

    holdingsTable.setItems(holdingsData);

    // Create a series for the portfolio value
    XYChart.Series<String, Number> portfolioSeries = new XYChart.Series<>();
    portfolioSeries.setName("Portfolio Value");

    // Get today's date
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

    // Add sample data with dates (last 7 days)
    for (int i = 6; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      String dateStr = date.format(formatter);
      double value = 1000 + (Math.random() * 500); // Random value between 1000 and 1500
      portfolioSeries.getData().add(new XYChart.Data<>(dateStr, value));
    }

    // Add the series to the chart
    portfolioChart.getData().add(portfolioSeries);

    // Customize the chart appearance
    portfolioChart.setCreateSymbols(false); // Removes the dots on the line
    portfolioChart.setLegendVisible(false);
    portfolioChart.setAnimated(true);

    // Set the title for the x-axis
    portfolioChart.getXAxis().setLabel("Date");
    portfolioChart.getYAxis().setLabel("Value ($)");

    // Initialize the allocation pie chart with sample data
    double totalValue = 100.0; // Total portfolio value for percentage calculation
    PieChart.Data rlusdData = new PieChart.Data("RLUSD", 40);
    PieChart.Data ust5yrData = new PieChart.Data("UST5YR", 25);
    PieChart.Data goldData = new PieChart.Data("GOLD", 20);
    PieChart.Data silverData = new PieChart.Data("SILVER", 15);

    allocationChart.getData().addAll(rlusdData, ust5yrData, goldData, silverData);

    // Customize the pie chart appearance
    allocationChart.setLegendVisible(false);
    allocationChart.setLabelsVisible(true);
    allocationChart.setStartAngle(90); // Start from top
    allocationChart.setClockwise(true);
    allocationChart.setAnimated(true);
    allocationChart.setLegendSide(Side.RIGHT);

    // Format labels with percentages
    for (PieChart.Data data : allocationChart.getData()) {
      double percentage = (data.getPieValue() / totalValue) * 100;
      // Format the label to show percentage
      data.setName(String.format("%s (%.1f%%)", data.getName(), percentage));
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
    Scene scene = toAddressTextField.getScene();
    VBox transferForm = (VBox) scene.lookup("#transferForm");

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
    Scene scene = toAddressTextField.getScene();
    VBox transferForm = (VBox) scene.lookup("#transferForm");
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
    Scene scene = toAddressTextField.getScene();
    VBox transferForm = (VBox) scene.lookup("#transferForm");

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
    toAddressTextField.setDisable(!enabled);
    quantityTextField.setDisable(!enabled);
    issuerComboBox.setDisable(!enabled);
    symbolComboBox.setDisable(!enabled);
    sendButton.setDisable(!enabled);
  }

  @FXML
  private void handleSendTransfer() {
    // Disable form controls and show spinner
    setFormControlsEnabled(false);
    showLoadingSpinner();

    // Create a background task for the transfer
    javafx.concurrent.Task<String> transferTask = new javafx.concurrent.Task<>() {
      @Override
      protected String call() throws Exception {
        return ApplicationContext.getInstance().getWalletService().getBlockchainService().transfer(
            ApplicationContext.getInstance().getWalletService().getWalletAddress(),
            Address.of(toAddressTextField.getText()),
            new BigDecimal(quantityTextField.getText()),
            ApplicationContext.getInstance().getAuthToken());
      }
    };

    // Handle task completion
    transferTask.setOnSucceeded(event -> {
      String transactionId = transferTask.getValue();
      hideLoadingSpinner();
      showToast("Transfer submitted successfully!", transactionId);

      // Clear the form
      toAddressTextField.clear();
      quantityTextField.clear();
      issuerComboBox.getSelectionModel().clearSelection();
      symbolComboBox.getSelectionModel().clearSelection();

      // Re-enable form controls
      setFormControlsEnabled(true);
    });

    // Handle task failure
    transferTask.setOnFailed(event -> {
      hideLoadingSpinner();
      showToast("Error: " + transferTask.getException().getMessage(), null);
      setFormControlsEnabled(true);
    });

    // Start the task in a background thread
    new Thread(transferTask).start();
  }

  @FXML
  private void copyAddressToClipboard(javafx.scene.input.MouseEvent event) {
    Label label = (Label) event.getSource();
    String address = label.getText();

    ClipboardContent content = new ClipboardContent();
    content.putString(address);
    Clipboard.getSystemClipboard().setContent(content);
  }
}