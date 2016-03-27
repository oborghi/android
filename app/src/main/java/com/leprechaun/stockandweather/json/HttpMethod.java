package com.leprechaun.stockandweather.json;

public enum HttpMethod {

    POST ("POST"),
    GET ("GET");

    private final String name;

    HttpMethod(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
