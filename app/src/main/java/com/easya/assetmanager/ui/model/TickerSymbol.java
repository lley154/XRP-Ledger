package com.easya.assetmanager.ui.model;

import javafx.scene.paint.Color;

public class TickerSymbol {
  private String symbol;
  private double price;
  private double changePercent;
  private Color changeColor;

  public TickerSymbol(String symbol, double price, double changePercent) {
    this.symbol = symbol;
    this.price = price;
    this.changePercent = changePercent;
    this.changeColor = changePercent >= 0 ? Color.GREEN : Color.RED;
  }

  public String getSymbol() {
    return symbol;
  }

  public double getPrice() {
    return price;
  }

  public double getChangePercent() {
    return changePercent;
  }

  public Color getChangeColor() {
    return changeColor;
  }

  public void updatePrice(double newPrice, double newChangePercent) {
    this.price = newPrice;
    this.changePercent = newChangePercent;
    this.changeColor = newChangePercent >= 0 ? Color.GREEN : Color.RED;
  }
}