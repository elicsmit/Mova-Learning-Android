package com.example.mova.ui.data.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserProfile {
    // Основная информация
    private String id;
    private String name = "Карыстальнік";
    private String email;
    private String bio = "";
    private String registrationDate;

    // Фотография
    private String photoPath;

    // Уровень и прогресс
    private LanguageLevel level = LanguageLevel.BEGINNER;
    private int currentLevelXP = 0;
    private int xpToNextLevel = 1000;

    // Статистика обучения
    private int streak = 0;
    private int maxStreak = 0;
    private int totalLearnedWords = 0;
    private int totalCompletedLessons = 0;
    private int totalCompletedArticles = 0;
    private int totalGrammarExercises = 0;
    private int totalTrainingSessions = 0;

    // Опыт и достижения
    private int totalXP = 0;
    private int dailyGoal = 50;
    private int weeklyXP = 0;
    private int monthlyXP = 0;

    // Текущий месяц
    private String currentMonth;

    // Коллекции
    private List<Achievement> achievements = new ArrayList<>();
    private List<String> favoriteWords = new ArrayList<>();
    private List<StudySession> studySessions = new ArrayList<>();

    // Конструкторы
    public UserProfile() {
        this.registrationDate = Calendar.getInstance().getTime().toString();
        updateCurrentMonth();
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public LanguageLevel getLevel() { return level; }
    public void setLevel(LanguageLevel level) { this.level = level; }

    public int getCurrentLevelXP() { return currentLevelXP; }
    public void setCurrentLevelXP(int currentLevelXP) { this.currentLevelXP = currentLevelXP; }

    public int getXpToNextLevel() { return xpToNextLevel; }
    public void setXpToNextLevel(int xpToNextLevel) { this.xpToNextLevel = xpToNextLevel; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }

    public int getTotalLearnedWords() { return totalLearnedWords; }
    public void setTotalLearnedWords(int totalLearnedWords) { this.totalLearnedWords = totalLearnedWords; }

    public int getTotalCompletedLessons() { return totalCompletedLessons; }
    public void setTotalCompletedLessons(int totalCompletedLessons) { this.totalCompletedLessons = totalCompletedLessons; }

    public int getTotalCompletedArticles() { return totalCompletedArticles; }
    public void setTotalCompletedArticles(int totalCompletedArticles) { this.totalCompletedArticles = totalCompletedArticles; }

    public int getTotalGrammarExercises() { return totalGrammarExercises; }
    public void setTotalGrammarExercises(int totalGrammarExercises) { this.totalGrammarExercises = totalGrammarExercises; }

    public int getTotalTrainingSessions() { return totalTrainingSessions; }
    public void setTotalTrainingSessions(int totalTrainingSessions) { this.totalTrainingSessions = totalTrainingSessions; }

    public int getTotalXP() { return totalXP; }
    public void setTotalXP(int totalXP) { this.totalXP = totalXP; }

    public int getDailyGoal() { return dailyGoal; }
    public void setDailyGoal(int dailyGoal) { this.dailyGoal = dailyGoal; }

    public int getWeeklyXP() { return weeklyXP; }
    public void setWeeklyXP(int weeklyXP) { this.weeklyXP = weeklyXP; }

    public int getMonthlyXP() { return monthlyXP; }
    public void setMonthlyXP(int monthlyXP) { this.monthlyXP = monthlyXP; }

    public String getCurrentMonth() {
        if (currentMonth == null) {
            updateCurrentMonth();
        }
        return currentMonth;
    }
    public void setCurrentMonth(String currentMonth) { this.currentMonth = currentMonth; }

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    public List<String> getFavoriteWords() { return favoriteWords; }
    public void setFavoriteWords(List<String> favoriteWords) { this.favoriteWords = favoriteWords; }

    public List<StudySession> getStudySessions() { return studySessions; }
    public void setStudySessions(List<StudySession> studySessions) { this.studySessions = studySessions; }

    // Методы
    public void updateCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        this.currentMonth = getMonthNameInBelarusian(month);
    }

    private String getMonthNameInBelarusian(int month) {
        switch (month) {
            case Calendar.JANUARY: return "Студзень";
            case Calendar.FEBRUARY: return "Люты";
            case Calendar.MARCH: return "Сакавік";
            case Calendar.APRIL: return "Красавік";
            case Calendar.MAY: return "Май";
            case Calendar.JUNE: return "Чэрвень";
            case Calendar.JULY: return "Ліпень";
            case Calendar.AUGUST: return "Жнівень";
            case Calendar.SEPTEMBER: return "Верасень";
            case Calendar.OCTOBER: return "Кастрычнік";
            case Calendar.NOVEMBER: return "Лістапад";
            case Calendar.DECEMBER: return "Снежань";
            default: return "Невядомы";
        }
    }

    public void addXP(int xp) {
        this.totalXP += xp;
        this.currentLevelXP += xp;
        this.weeklyXP += xp;
        this.monthlyXP += xp;

        // Проверка повышения уровня
        if (currentLevelXP >= xpToNextLevel && level.getNextLevel() != null) {
            levelUp();
        }
    }

    private void levelUp() {
        LanguageLevel nextLevel = level.getNextLevel();
        if (nextLevel != null) {
            this.level = nextLevel;
            this.currentLevelXP = currentLevelXP - xpToNextLevel;
            this.xpToNextLevel = nextLevel.getXpRequired();
        }
    }

    public void addFavoriteWord(String word) {
        if (word != null && !word.trim().isEmpty() && !favoriteWords.contains(word.trim())) {
            favoriteWords.add(word.trim());
        }
    }

    public void removeFavoriteWord(String word) {
        favoriteWords.remove(word);
    }
}