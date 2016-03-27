package com.leprechaun.stockandweather.entity;

public enum StockType {

    BOVESPA ("bovespa"),
    DOLAR ("dolar"),
    EURO ("euro");

    private final String name;

    StockType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    public String capitalize() {
        return Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
    }
}
