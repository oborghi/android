package com.leprechaun.stockandweather.entity;

/**
 * Created by oborghi on 17/03/16.
 */
public class Stock {
    private StockType type;
    private double value;
    private double variation;

    public StockType getType() {
        return type;
    }

    public void setType(StockType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getVariation() {
        return variation;
    }

    public void setVariation(double variation) {
        this.variation = variation;
    }
}
