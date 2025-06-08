package com.easya.assetmanager.ui.controller;

import com.easya.assetmanager.ui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class HeaderController {
  @FXML
  private Button themeToggleBtn;

  @FXML
  private void handleThemeToggle() {
    Scene scene = themeToggleBtn.getScene();
    MainApp.toggleTheme(scene);
  }
}