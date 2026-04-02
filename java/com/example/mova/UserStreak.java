package com.example.mova;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserStreak {
    private int id;
    private int userId;
    private int currentStreak;
    private int maxStreak;
    private String lastActivityDate;
    private String lastLoginDate;

    // Конструкторы
    public UserStreak() {}

    public UserStreak(int userId) {
        this.userId = userId;
        this.currentStreak = 0;
        this.maxStreak = 0;
        this.lastActivityDate = getCurrentDate();
        this.lastLoginDate = getCurrentDate();
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }

    public String getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(String lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public String getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(String lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    // Вспомогательные методы
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public boolean isTodayActive() {
        String today = getCurrentDate();
        return today.equals(lastActivityDate);
    }

    public boolean isYesterdayActive() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String yesterday = sdf.format(calendar.getTime());

        return yesterday.equals(lastActivityDate);
    }

    public void updateStreak() {
        String today = getCurrentDate();

        if (isTodayActive()) {
            // Уже активен сегодня - ничего не делаем
            return;
        }

        if (isYesterdayActive()) {
            // Активен вчера - увеличиваем стрик
            currentStreak++;
        } else {
            // Пропущен день - сбрасываем стрик
            currentStreak = 1;
        }

        // Обновляем максимальный стрик
        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }

        lastActivityDate = today;
    }

    public void updateLogin() {
        lastLoginDate = getCurrentDate();
    }
}