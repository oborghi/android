package com.leprechaun.stockandweather.entity;

public enum StockValues {

    COTACAO ("cotacao"),
    VARIACAO ("variacao");

    private final String name;

    StockValues(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

}
