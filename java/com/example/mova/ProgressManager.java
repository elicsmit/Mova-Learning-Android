package com.example.mova;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mova.UserProgress;
import com.example.mova.UserStreak;
import com.example.mova.DatabaseHelper;
import com.example.mova.ui.data.models.DataManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// ProgressManager.java
public class ProgressManager {
    private static ProgressManager instance;
    private Context context;
    private DatabaseHelper databaseHelper;
    private SharedPreferences prefs;

    private ProgressManager(Context context) {
        this.context = context.getApplicationContext();
        this.databaseHelper = new DatabaseHelper(context);
        this.prefs = context.getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
    }

    public static synchronized ProgressManager getInstance(Context context) {
        if (instance == null) {
            instance = new ProgressManager(context);
        }
        return instance;
    }

    // Основные методы для работы с прогрессом

    public void updateUserActivity(int userId) {
        String today = getTodayDate();

        new Thread(() -> {
            try {
                // Проверяем, был ли сегодня уже записан активный день
                if (!databaseHelper.isActiveDayRecorded(userId, today)) {
                    // Добавляем запись об активном дне
                    databaseHelper.addActiveDay(userId, today);

                    // Обновляем стрик
                    updateStreak(userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateStreak(int userId) {
        String today = getTodayDate();
        String yesterday = getYesterdayDate();

        int currentStreak = databaseHelper.getCurrentStreak(userId);
        int maxStreak = databaseHelper.getMaxStreak(userId);

        // Проверяем, был ли вчера активный день
        if (databaseHelper.isActiveDayRecorded(userId, yesterday)) {
            currentStreak++;
        } else {
            // Проверяем, не прервался ли стрик (больше 1 дня пропуска)
            if (!isConsecutiveDay(userId, today)) {
                currentStreak = 1;
            }
        }

        // Обновляем максимальный стрик
        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }

        // Сохраняем в базу данных
        databaseHelper.updateStreak(userId, currentStreak, maxStreak);
    }

    private boolean isConsecutiveDay(int userId, String today) {
        // Получаем дату последней активности
        String lastActiveDate = databaseHelper.getLastActiveDate(userId);
        if (lastActiveDate == null) return false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date lastDate = sdf.parse(lastActiveDate);
            Date todayDate = sdf.parse(today);

            if (lastDate != null && todayDate != null) {
                long diff = todayDate.getTime() - lastDate.getTime();
                return diff <= 2 * 24 * 60 * 60 * 1000; // Не более 2 дней разницы
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addLearnedWords(int userId, int count) {
        new Thread(() -> {
            try {
                databaseHelper.addLearnedWords(userId, count);

                // Добавляем опыт за выученные слова
                int experience = count * 10;
                databaseHelper.addExperience(userId, experience);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void addCompletedLesson(int userId) {
        new Thread(() -> {
            try {
                databaseHelper.addCompletedLesson(userId);

                // Добавляем опыт за урок
                databaseHelper.addExperience(userId, 50);

                // Обновляем активность
                updateUserActivity(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void updateUserName(int userId, String newName) {
        new Thread(() -> {
            try {
                databaseHelper.updateUserName(userId, newName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Вспомогательные методы
    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }


    public Map<String, Integer> getStreakStats(int userId) {
        Map<String, Integer> stats = new HashMap<>();

        try {
            // Используем реальные данные из БД
            int totalActiveDays = databaseHelper.getUserActiveDays(userId);
            int currentStreak = databaseHelper.getCurrentStreak(userId);
            int maxStreak = databaseHelper.getMaxStreak(userId);

            stats.put("total_active_days", totalActiveDays);
            stats.put("current_streak", currentStreak);
            stats.put("max_streak", maxStreak);

        } catch (Exception e) {
            e.printStackTrace();
            // Значения по умолчанию только если ошибка
            stats.put("total_active_days", 0);
            stats.put("current_streak", 0);
            stats.put("max_streak", 0);
        }

        return stats;
    }

    public UserProgress getUserStats(int userId) {
        UserProgress progress = new UserProgress();

        try {
            // Используем реальные данные из БД
            int learnedWords = databaseHelper.getRealLearnedWordsCount(userId);
            int completedLessons = databaseHelper.getRealCompletedLessonsCount(userId);
            int totalXp = databaseHelper.getRealTotalExperience(userId);

            progress.setLearnedWords(learnedWords);
            progress.setCompletedLessons(completedLessons);
            progress.setTotalXp(totalXp);

        } catch (Exception e) {
            e.printStackTrace();
            // Только в случае ошибки - значения по умолчанию
            progress.setLearnedWords(0);
            progress.setCompletedLessons(0);
            progress.setTotalXp(0);
        }

        return progress;
    }
    private String getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(cal.getTime());
    }
}