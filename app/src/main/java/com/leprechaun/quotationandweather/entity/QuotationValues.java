package com.leprechaun.quotationandweather.entity;

/**
 * Created by oborghi on 17/03/16.
 */
public enum QuotationValues {
    /**
     * Tipos de cotação
     */
    COTACAO ("cotacao"),
    VARIACAO ("variacao");

    private final String name;

    QuotationValues(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

}
