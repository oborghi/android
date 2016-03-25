package com.leprechaun.stockandweather.json;

/**
 * Created by oborghi on 16/03/16.
 */
public enum HttpMethod {

    /**
     * Tipo de requisição
     */
    POST ("POST"),
    GET ("GET");

    private final String name;

    HttpMethod(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName != null) && name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
