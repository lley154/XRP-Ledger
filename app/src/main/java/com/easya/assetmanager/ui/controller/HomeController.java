package com.easya.assetmanager.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class HomeController {
  @FXML
  private ImageView homeImage;

  @FXML
  private void initialize() {
    System.out.println("HomeController initialize() called");
    if (homeImage != null) {
      System.out.println("ImageView is not null");
      Image image = homeImage.getImage();
      if (image != null) {
        System.out.println("Image loaded successfully. Width: " + image.getWidth() + ", Height: " + image.getHeight());
      } else {
        System.out.println("Image is null - image not loaded");
      }
      homeImage.setVisible(true);
    } else {
      System.out.println("ImageView is null - FXML injection failed");
    }
  }

  @FXML
  private void handleTransferClick() {
    // Handle transfer click - you might want to create a separate transfer.fxml
    System.out.println("Transfer clicked");
  }
}