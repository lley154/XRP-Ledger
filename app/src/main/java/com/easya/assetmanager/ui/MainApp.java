package com.easya.assetmanager.ui;

import com.easya.assetmanager.context.ApplicationContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
  public static boolean isDarkMode = false;

  // Window size constants
  private static final double MIN_WIDTH = 950;
  private static final double MIN_HEIGHT = 700;
  private static final double DEFAULT_WIDTH = 1024;
  private static final double DEFAULT_HEIGHT = 768;
  private static final double MAX_WIDTH = 1920;
  private static final double MAX_HEIGHT = 1080;

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Initialize application context
    ApplicationContext.getInstance();

    // Load and setup UI
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/navigation.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root);
    applyTheme(scene);

    // Set window size constraints
    primaryStage.setMinWidth(MIN_WIDTH);
    primaryStage.setMinHeight(MIN_HEIGHT);
    primaryStage.setMaxWidth(MAX_WIDTH);
    primaryStage.setMaxHeight(MAX_HEIGHT);

    // Set default window size
    primaryStage.setWidth(DEFAULT_WIDTH);
    primaryStage.setHeight(DEFAULT_HEIGHT);

    primaryStage.setTitle("Asset Management Platform");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void toggleTheme(Scene scene) {
    isDarkMode = !isDarkMode;
    applyTheme(scene);
  }

  private static void applyTheme(Scene scene) {
    if (isDarkMode) {
      scene.getStylesheets().clear();
      scene.getStylesheets().add(MainApp.class.getResource("/styles/dark-mode.css").toExternalForm());
    } else {
      scene.getStylesheets().clear();
      scene.getStylesheets().add(MainApp.class.getResource("/styles/light-mode.css").toExternalForm());
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}