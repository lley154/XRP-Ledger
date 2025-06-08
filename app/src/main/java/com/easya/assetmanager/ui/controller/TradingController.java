package com.easya.assetmanager.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TradingController {

  @FXML
  private LineChart<String, Number> assetChart;

  public void initialize() {
    // Create a series for the portfolio value
    XYChart.Series<String, Number> assetSeries = new XYChart.Series<>();
    assetSeries.setName("Asset Value");

    // Get today's date
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

    // Add sample data with dates (last 7 days)
    for (int i = 6; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      String dateStr = date.format(formatter);
      double value = 1000 + (Math.random() * 500); // Random value between 1000 and 1500
      assetSeries.getData().add(new XYChart.Data<>(dateStr, value));
    }

    // Add the series to the chart
    assetChart.getData().add(assetSeries);

    // Customize the chart appearance
    assetChart.setCreateSymbols(false); // Removes the dots on the line
    assetChart.setLegendVisible(false);
    assetChart.setAnimated(true);

    // Set the title for the x-axis
    assetChart.getXAxis().setLabel("Date");
    assetChart.getYAxis().setLabel("Value ($)");
  }

}