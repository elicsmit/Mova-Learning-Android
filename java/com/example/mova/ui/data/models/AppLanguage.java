package com.example.mova.ui.data.models;

// AppLanguage.java - языки интерфейса

public enum AppLanguage {
    BELARUSIAN("Беларуская", "be"),
    RUSSIAN("Русский", "ru"),
    ENGLISH("English", "en");

    private final String displayName;
    private final String code;

    AppLanguage(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() { return displayName; }
    public String getCode() { return code; }
}