package com.leprechaun.stockandweather.entity;

/**
 * Created by oborghi on 17/03/16.
 */
public enum StockValues {
    /**
     * Tipos de cotação
     */
    COTACAO ("cotacao"),
    VARIACAO ("variacao");

    private final String name;

    StockValues(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}
