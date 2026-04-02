package com.example.mova;

public class GrammarTopic {
    private String title;
    private String description;
    private boolean favorite;

    // Конструктор с ТРЕМЯ параметрами
    public GrammarTopic(String title, String description, boolean favorite) {
        this.title = title;
        this.description = description;
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}