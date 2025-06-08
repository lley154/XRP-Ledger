package com.easya.assetmanager.ui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public class NavigationController {
  @FXML
  private Accordion navigationAccordion;

  private Map<String, TitledPane> pageToPaneMap = new HashMap<>();
  private boolean isInitialized = false;

  @FXML
  public void initialize() {
    if (!isInitialized) {
      // Set the accordion to only allow one expanded pane at a time
      navigationAccordion.setExpandedPane(null);

      // Map each page to its corresponding TitledPane
      for (TitledPane pane : navigationAccordion.getPanes()) {
        String title = pane.getText().toLowerCase();
        pageToPaneMap.put(title, pane);
        System.out.println("Mapped page: " + title + " to pane: " + pane);
      }

      isInitialized = true;
      loadFXML("/fxml/home.fxml", "home");
    }
  }

  @FXML
  public void handleHomeClick() {
    loadFXML("/fxml/home.fxml", "home");
  }

  @FXML
  public void handlePortfolioClick() {
    loadFXML("/fxml/portfolio.fxml", "portfolio");
  }

  @FXML
  public void handleTradingClick() {
    loadFXML("/fxml/trading.fxml", "trading");
  }

  @FXML
  public void handleAssetManagementClick() {
    loadFXML("/fxml/asset.fxml", "asset management");
  }

  @FXML
  public void handleReportsClick() {
    loadFXML("/fxml/reporting.fxml", "reports");
  }

  private void loadFXML(String fxmlPath, String pageName) {
    try {

      // Load the new content
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
      Parent root = loader.load();

      // First, expand the corresponding TitledPane
      TitledPane pane = pageToPaneMap.get(pageName.toLowerCase());
      System.out.println("Loading page: " + pageName + ", found pane: " + pane);

      if (pane != null) {
        // Use Platform.runLater to ensure UI updates happen after the current event
        javafx.application.Platform.runLater(() -> {
          navigationAccordion.setExpandedPane(pane);
          System.out.println("Set expanded pane to: " + pane.getText());

          // Get the current scene and update its root
          Scene currentScene = navigationAccordion.getScene();
          if (currentScene != null) {
            // Update the content while preserving the navigation
            VBox mainContent = (VBox) currentScene.lookup("#mainContent");
            if (mainContent != null) {
              mainContent.getChildren().clear();
              mainContent.getChildren().add(root);
            }
          }
        });
      } else {
        System.out.println("No pane found for page: " + pageName);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}