package com.leprechaun.stockandweather.entity;

/**
 * Created by oborghi on 17/03/16.
 */
public enum StockType {
    /**
     * Tipos de cotação
     */
    BOVESPA ("bovespa"),
    DOLAR ("dolar"),
    EURO ("euro");

    private final String name;

    StockType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public String capitalize() {
        return Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
    }
}
