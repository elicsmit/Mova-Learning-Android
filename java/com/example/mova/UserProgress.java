package com.example.mova;

public class UserProgress {
    private int id;
    private int userId;
    private int learnedWords;
    private int completedLessons;
    private int completedArticles;
    private int grammarExercises;
    private int totalXp;
    private long lastUpdated;
    private int streak; // Добавляем поле для стрика

    // Конструкторы
    public UserProgress() {}

    public UserProgress(int userId) {
        this.userId = userId;
        this.learnedWords = 0;
        this.completedLessons = 0;
        this.completedArticles = 0;
        this.grammarExercises = 0;
        this.totalXp = 0;
        this.lastUpdated = System.currentTimeMillis();
        this.streak = 0;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getLearnedWords() { return learnedWords; }
    public void setLearnedWords(int learnedWords) { this.learnedWords = learnedWords; }

    public int getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }

    public int getCompletedArticles() { return completedArticles; }
    public void setCompletedArticles(int completedArticles) { this.completedArticles = completedArticles; }

    public int getGrammarExercises() { return grammarExercises; }
    public void setGrammarExercises(int grammarExercises) { this.grammarExercises = grammarExercises; }

    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    // Добавляем методы для стрика
    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    // Вспомогательные методы
    public void addLearnedWords(int count) {
        this.learnedWords += count;
        this.totalXp += count * 10; // 10 XP за каждое слово
        this.lastUpdated = System.currentTimeMillis();
    }

    public void addCompletedLesson() {
        this.completedLessons++;
        this.totalXp += 50; // 50 XP за урок
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getTotalActivities() {
        return learnedWords + completedLessons + completedArticles + grammarExercises;
    }
}