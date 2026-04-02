package com.example.mova;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class UserDataManager {
    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;
    private Gson gson;

    public UserDataManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        dbHelper = new DatabaseHelper(context);
        gson = new Gson();
    }

    public UserStats getUserStats() {
        UserStats stats = new UserStats();

        // Загружаем данные из SharedPreferences
        stats.setCompletedLessons(prefs.getInt("completed_lessons", 0));
        stats.setLearnedWords(prefs.getInt("learned_words", 0));
        stats.setExperience(prefs.getInt("experience", 0));
        stats.setCurrentStreak(prefs.getInt("current_streak", 0));
        stats.setMaxStreak(prefs.getInt("max_streak", 0));
        stats.setUserName(prefs.getString("user_name", "Міхась"));

        // Загружаем активные дни
        String activeDaysJson = prefs.getString("active_days", "[]");
        Type setType = new TypeToken<HashSet<String>>(){}.getType();
        Set<String> activeDays = gson.fromJson(activeDaysJson, setType);
        if (activeDays != null) {
            stats.setActiveDays(activeDays);
        }

        return stats;
    }

    public void saveUserStats(UserStats stats) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("completed_lessons", stats.getCompletedLessons());
        editor.putInt("learned_words", stats.getLearnedWords());
        editor.putInt("experience", stats.getExperience());
        editor.putInt("current_streak", stats.getCurrentStreak());
        editor.putInt("max_streak", stats.getMaxStreak());
        editor.putString("user_name", stats.getUserName());

        // Сохраняем активные дни
        String activeDaysJson = gson.toJson(stats.getActiveDays());
        editor.putString("active_days", activeDaysJson);

        editor.apply();
    }

    // Методы для обновления статистики
    public void addCompletedLesson() {
        UserStats stats = getUserStats();
        stats.addCompletedLesson();
        saveUserStats(stats);
    }

    public void addLearnedWord() {
        UserStats stats = getUserStats();
        stats.addLearnedWord();
        saveUserStats(stats);
    }

    public void addPracticeCompleted() {
        UserStats stats = getUserStats();
        stats.addPracticeCompleted();
        saveUserStats(stats);
    }

    public void addWordToDictionary() {
        UserStats stats = getUserStats();
        stats.addWordToDictionary();
        saveUserStats(stats);
    }

    public void updateUserName(String name) {
        UserStats stats = getUserStats();
        stats.setUserName(name);
        saveUserStats(stats);
    }
}