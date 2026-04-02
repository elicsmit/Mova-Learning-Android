package com.example.mova;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserStats {
    private int completedLessons;
    private int learnedWords;
    private int experience;
    private Set<String> activeDays; // Используем Set для избежания дубликатов
    private int currentStreak;
    private int maxStreak;
    private String userName;

    public UserStats() {
        this.completedLessons = 0;
        this.learnedWords = 0;
        this.experience = 0;
        this.activeDays = new HashSet<>();
        this.currentStreak = 0;
        this.maxStreak = 0;
        this.userName = "Міхась"; // Имя по умолчанию
    }

    // Геттеры и сеттеры
    public int getCompletedLessons() { return completedLessons; }
    public int getLearnedWords() { return learnedWords; }
    public int getExperience() { return experience; }
    public Set<String> getActiveDays() { return activeDays; }
    public int getCurrentStreak() { return currentStreak; }
    public int getMaxStreak() { return maxStreak; }
    public String getUserName() { return userName; }

    public void setCompletedLessons(int completedLessons) { this.completedLessons = completedLessons; }
    public void setLearnedWords(int learnedWords) { this.learnedWords = learnedWords; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setActiveDays(Set<String> activeDays) { this.activeDays = activeDays; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }
    public void setUserName(String userName) { this.userName = userName; }

    // Методы для обновления статистики
    public void addCompletedLesson() {
        completedLessons++;
        addExperience(50); // Больше опыта за урок
        markTodayAsActive();
    }

    public void addLearnedWord() {
        learnedWords++;
        addExperience(10); // Опыт за выученное слово
    }

    public void addPracticeCompleted() {
        addExperience(20);
        markTodayAsActive();
    }

    public void addWordToDictionary() {
        addExperience(5); // Небольшой опыт за добавление слова
    }

    private void addExperience(int exp) {
        experience += exp;
    }

    private void markTodayAsActive() {
        String today = getTodayDate();

        if (!activeDays.contains(today)) {
            activeDays.add(today);
            updateStreak();
        }
    }

    private void updateStreak() {
        String today = getTodayDate();
        String yesterday = getYesterdayDate();

        if (activeDays.contains(yesterday)) {
            currentStreak++;
        } else {
            // Проверяем, не прервался ли стрик
            if (!isTodayYesterday(today, yesterday)) {
                currentStreak = 1;
            }
        }

        // Обновляем максимальный стрик
        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    private boolean isTodayYesterday(String today, String yesterday) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date todayDate = sdf.parse(today);
            Date yesterdayDate = sdf.parse(yesterday);

            if (todayDate != null && yesterdayDate != null) {
                long diff = todayDate.getTime() - yesterdayDate.getTime();
                return diff == 24 * 60 * 60 * 1000; // Разница ровно 1 день
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getActiveDaysCount() {
        return activeDays.size();
    }

    public String getLevel() {
        if (experience >= 5000) return "Эксперт";
        if (experience >= 2000) return "Прасунуты";
        if (experience >= 1000) return "Сярэдні";
        if (experience >= 300) return "Пачатковы";
        return "Новачок";
    }

    public String getLevelInBelarusian() {
        if (experience >= 5000) return "Эксперт";
        if (experience >= 2000) return "Прасунуты";
        if (experience >= 1000) return "Сярэдні";
        if (experience >= 300) return "Пачатковы";
        return "Новачок";
    }

    public int getLevelProgress() {
        int currentLevelExp = experience;
        int levelThreshold = 0;

        if (experience >= 5000) {
            levelThreshold = 5000;
        } else if (experience >= 2000) {
            levelThreshold = 2000;
        } else if (experience >= 1000) {
            levelThreshold = 1000;
        } else if (experience >= 300) {
            levelThreshold = 300;
        }

        int nextLevelThreshold = getNextLevelThreshold();
        int progress = (int) ((float) (experience - levelThreshold) / (nextLevelThreshold - levelThreshold) * 100);
        return Math.min(Math.max(progress, 0), 100);
    }

    private int getNextLevelThreshold() {
        if (experience < 300) return 300;
        if (experience < 1000) return 1000;
        if (experience < 2000) return 2000;
        if (experience < 5000) return 5000;
        return 5000; // Максимальный уровень
    }

    public String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("be"));
        return sdf.format(new Date());
    }

    // Метод для сброса статистики (по желанию)
    public void resetStats() {
        this.completedLessons = 0;
        this.learnedWords = 0;
        this.experience = 0;
        this.activeDays.clear();
        this.currentStreak = 0;
        this.maxStreak = 0;
    }
}