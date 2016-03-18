package com.leprechaun.quotationandweather.entity;

/**
 * Created by oborghi on 17/03/16.
 */
public class Quotation {
    private QuotationType type;
    private double value;
    private double variation;

    public QuotationType getType() {
        return type;
    }

    public void setType(QuotationType type) {
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
