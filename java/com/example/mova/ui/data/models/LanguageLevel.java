package com.example.mova.ui.data.models;

// LanguageLevel.java - уровни языка

public enum LanguageLevel {
    BEGINNER("Пачатковец", 0, 1000),
    ELEMENTARY("Асновы", 1000, 2000),
    INTERMEDIATE("Сярэдні", 3000, 4000),
    UPPER_INTERMEDIATE("Вышэйшы сярэдні", 7000, 5000),
    ADVANCED("Прасунуты", 12000, 0);

    private final String displayName;
    private final int xpRequired;
    private final int xpToNextLevel;

    LanguageLevel(String displayName, int xpRequired, int xpToNextLevel) {
        this.displayName = displayName;
        this.xpRequired = xpRequired;
        this.xpToNextLevel = xpToNextLevel;
    }

    public String getDisplayName() { return displayName; }
    public int getXpRequired() { return xpRequired; }
    public int getXpToNextLevel() { return xpToNextLevel; }

    public LanguageLevel getNextLevel() {
        if (this.ordinal() < values().length - 1) {
            return values()[this.ordinal() + 1];
        }
        return null;
    }
}