package com.bakikocak.challenge_wafer.model;

public class Country {

    String name, currency, language;

    public Country(String name, String currency, String language) {
        this.name = name;
        this.currency = currency;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
