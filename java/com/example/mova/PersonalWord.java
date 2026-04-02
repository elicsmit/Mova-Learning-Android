package com.example.mova;

import java.util.Date;

public class PersonalWord {
    private String belarusianWord;
    private String russianTranslation;
    private String englishTranslation;
    private String example;
    private boolean isLearned;
    private String imageUrl; // URL или путь к картинке
    private int progress; // Прогресс изучения (0-100)

    private String belarusian;
    private String russian;
    private String english;


    public PersonalWord(String belarusianWord, String russianTranslation,
                        String englishTranslation, String example, boolean isLearned) {
        this.belarusianWord = belarusianWord;
        this.russianTranslation = russianTranslation;
        this.englishTranslation = englishTranslation;
        this.example = example;
        this.isLearned = isLearned;
        this.progress = isLearned ? 100 : 0;
    }

    // Геттеры и сеттеры

    public String getWord() {
        return belarusian;
    }

    public String getTranslation() {
        return russian;
    }

    public String getCategory() {
        return ""; // или реализуйте логику категорий
    }

    public Date getDateAdded() {
        return new Date(); // или храните дату добавления
    }
    public String getBelarusianWord() { return belarusianWord; }
    public String getRussianTranslation() { return russianTranslation; }
    public String getEnglishTranslation() { return englishTranslation; }
    public String getImageUrl() { return imageUrl; }

    public String getBelarusian() { return belarusian; }
    public String getRussian() { return russian; }
    public String getEnglish() { return english; }
    public String getExample() { return example; }
    public boolean isLearned() { return isLearned; }
    public int getProgress() { return progress; }

    public void setLearned(boolean learned) {
        isLearned = learned;
        progress = learned ? 100 : Math.min(progress, 99);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress >= 100) {
            isLearned = true;
        }
    }
}