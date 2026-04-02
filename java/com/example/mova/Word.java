package com.example.mova;

public class Word {

    private long id;
    private String belarusian;
    private String russian;
    private String english;
    private int audioResourceId;
    private String example;
    private boolean isLearned;
    private int progress;
    private String original;
    private String translation;

    public Word(){}
    // Конструктор для обычных слов
    public Word(String belarusian, String russian, String english) {
        this.belarusian = belarusian;
        this.russian = russian;
        this.english = english;
        this.audioResourceId = 0;
        this.example = "";
        this.isLearned = false;
        this.progress = 0;
    }

    // Конструктор для слов с аудио
    public Word(String belarusian, String russian, String english, int audioResourceId) {
        this.belarusian = belarusian;
        this.russian = russian;
        this.english = english;
        this.audioResourceId = audioResourceId;
        this.example = "";
        this.isLearned = false;
        this.progress = 0;
    }

    public Word(String belarusian, String russian, String english, String example, boolean isLearned) {
        this.belarusian = belarusian;
        this.russian = russian;
        this.english = english;
        this.example = example;
        this.isLearned = isLearned;
    }

    // Полный конструктор


    // Геттеры
    public String getBelarusian() { return belarusian; }
    public String getRussian() { return russian; }
    public String getEnglish() { return english; }
    public int getAudioResourceId() { return audioResourceId; }
    public String getExample() { return example; }
    public boolean isLearned() { return isLearned; }
    public int getProgress() { return progress; }

    // Сеттеры
    public void setAudioResourceId(int audioResourceId) { this.audioResourceId = audioResourceId; }
    public void setExample(String example) { this.example = example; }
    public void setLearned(boolean learned) { isLearned = learned; }
    public void setProgress(int progress) { this.progress = progress; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
    }

    public void setAddedDate(String addedDate) {
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}