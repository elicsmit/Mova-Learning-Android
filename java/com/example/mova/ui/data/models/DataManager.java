package com.example.mova.ui.data.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.mova.UserProgress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataManager {
    public static DataManager Companion;
    private SharedPreferences prefs;
    private UserProfile userProfile;
    private Gson gson;
    private Context appContext;

    private static final String PREFS_NAME = "MovaAppPrefs";
    private static final String KEY_USER_PROFILE = "user_profile";
    private static final String KEY_LAST_MONTH = "last_month";
    private static final String KEY_APP_INITIALIZED = "app_initialized";

    private static volatile DataManager instance;

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager(context);
                }
            }
        }
        return instance;
    }

    private DataManager(Context context) {
        appContext = context.getApplicationContext();
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        initializeData();
    }

    // Инициализация данных
    private void initializeData() {
        if (!prefs.getBoolean(KEY_APP_INITIALIZED, false)) {
            createDefaultUserProfile();
            prefs.edit().putBoolean(KEY_APP_INITIALIZED, true).apply();
        } else {
            loadUserProfile();
        }
    }

    // Создание профиля по умолчанию
    private void createDefaultUserProfile() {
        userProfile = new UserProfile();
        userProfile.setId(generateUserId());
        userProfile.setName("Карыстальнік");
        userProfile.setEmail("");
        userProfile.setBio("Я вучу беларускую мову!");
        userProfile.updateCurrentMonth();
        userProfile.setAchievements(getDefaultAchievements());
        userProfile.setLevel(LanguageLevel.BEGINNER);
        userProfile.setCurrentLevelXP(0);
        userProfile.setXpToNextLevel(1000);
        userProfile.setDailyGoal(50);
        saveUserProfileToPrefs();
    }

    // Загрузка профиля из SharedPreferences
    private void loadUserProfile() {
        String profileJson = prefs.getString(KEY_USER_PROFILE, null);
        if (profileJson != null) {
            try {
                TypeToken<UserProfile> userProfileType = new TypeToken<UserProfile>() {};
                userProfile = gson.fromJson(profileJson, userProfileType.getType());
                // Обновляем месяц при загрузке
                if (userProfile != null) {
                    userProfile.updateCurrentMonth();
                } else {
                    createDefaultUserProfile();
                }
            } catch (Exception e) {
                e.printStackTrace();
                createDefaultUserProfile();
            }
        } else {
            createDefaultUserProfile();
        }
    }

    // Сохранение профиля в SharedPreferences
    private void saveUserProfileToPrefs() {
        if (userProfile != null) {
            String profileJson = gson.toJson(userProfile);
            prefs.edit().putString(KEY_USER_PROFILE, profileJson).apply();
        }
    }

    // Генерация ID пользователя
    private String generateUserId() {
        return "user_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // === PUBLIC METHODS ===

    // Получить профиль пользователя
    public UserProfile getUserProfile() {
        if (userProfile == null) {
            loadUserProfile();
        }
        return userProfile;
    }

    // Сохранить профиль пользователя
    public void saveUserProfile(UserProfile profile) {
        this.userProfile = profile;
        saveUserProfileToPrefs();
    }

    // Обновить имя пользователя
    public void updateUserName(String newName) {
        if (userProfile != null && newName != null && !newName.trim().isEmpty()) {
            userProfile.setName(newName.trim());
            saveUserProfileToPrefs();
        }
    }

    // Обновить фото профиля
    public void updateUserPhoto(String photoPath) {
        if (userProfile != null) {
            userProfile.setPhotoPath(photoPath);
            saveUserProfileToPrefs();
        }
    }

    // Обновить статистику
    public void updateUserStats(String activityType, int xpEarned, List<String> learnedWords) {
        if (userProfile != null) {
            switch (activityType) {
                case "lesson":
                    userProfile.setTotalCompletedLessons(userProfile.getTotalCompletedLessons() + 1);
                    break;
                case "article":
                    userProfile.setTotalCompletedArticles(userProfile.getTotalCompletedArticles() + 1);
                    break;
                case "grammar":
                    userProfile.setTotalGrammarExercises(userProfile.getTotalGrammarExercises() + 1);
                    break;
                case "training":
                    userProfile.setTotalTrainingSessions(userProfile.getTotalTrainingSessions() + 1);
                    break;
            }

            if (learnedWords != null) {
                userProfile.setTotalLearnedWords(userProfile.getTotalLearnedWords() + learnedWords.size());
            }

            userProfile.addXP(xpEarned);
            saveUserProfileToPrefs();
        }
    }

    // Обновить серию дней
    public void updateStreak() {
        if (userProfile != null) {
            userProfile.setStreak(userProfile.getStreak() + 1);
            if (userProfile.getStreak() > userProfile.getMaxStreak()) {
                userProfile.setMaxStreak(userProfile.getStreak());
            }
            saveUserProfileToPrefs();
        }
    }

    // Сбросить серию дней
    public void resetStreak() {
        if (userProfile != null) {
            userProfile.setStreak(0);
            saveUserProfileToPrefs();
        }
    }

    // Добавить слово в избранное
    public void addFavoriteWord(String word) {
        if (userProfile == null || word == null || word.trim().isEmpty()) return;

        String trimmedWord = word.trim();
        if (!userProfile.getFavoriteWords().contains(trimmedWord)) {
            userProfile.getFavoriteWords().add(trimmedWord);
            saveUserProfileToPrefs();
        }
    }

    // Удалить слово из избранного
    public void removeFavoriteWord(String word) {
        if (userProfile != null) {
            userProfile.getFavoriteWords().remove(word);
            saveUserProfileToPrefs();
        }
    }

    // Получить избранные слова
    public List<String> getFavoriteWords() {
        return userProfile != null ? userProfile.getFavoriteWords() : new ArrayList<>();
    }

    // Проверить, есть ли слово в избранном
    public boolean isWordFavorite(String word) {
        return userProfile != null && userProfile.getFavoriteWords().contains(word);
    }

    // Получить текущий месяц
    public String getCurrentMonth() {
        if (userProfile != null) {
            userProfile.updateCurrentMonth();
            return userProfile.getCurrentMonth();
        }
        return "Невядомы";
    }

    // Проверить, сменился ли месяц
    public boolean hasMonthChanged() {
        String lastMonth = prefs.getString(KEY_LAST_MONTH, "");
        String currentMonth = getCurrentMonth();

        if (!lastMonth.equals(currentMonth)) {
            prefs.edit().putString(KEY_LAST_MONTH, currentMonth).apply();
            onMonthChanged(currentMonth);
            return true;
        }
        return false;
    }

    // Обработка смены месяца
    private void onMonthChanged(String newMonth) {
        if (userProfile != null) {
            userProfile.setMonthlyXP(0);
            saveUserProfileToPrefs();
        }
    }

    // Получить достижения по умолчанию
    public List<Achievement> getDefaultAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("Першы крок", "Завершыць першы ўрок", android.R.drawable.ic_menu_help, true));
        achievements.add(new Achievement("Стралец", "10 дзён падряд", android.R.drawable.star_big_on, false));
        achievements.add(new Achievement("Лексiкон", "Вывучыць 100 словаў", android.R.drawable.ic_lock_lock, false));
        achievements.add(new Achievement("Граматык", "Выканаць 50 практыкаванняў", android.R.drawable.ic_menu_edit, false));
        achievements.add(new Achievement("Чытач", "Прачытаць 10 артыкулаў", android.R.drawable.ic_menu_agenda, false));
        achievements.add(new Achievement("Марафонец", "30 дзён запар", android.R.drawable.ic_media_play, false));
        return achievements;
    }

    // === МЕТОДЫ ДЛЯ ДОСТИЖЕНИЙ ===

    // Получить все достижения
    public List<Achievement> getAchievements() {
        if (userProfile == null) {
            loadUserProfile();
        }
        return userProfile != null ? userProfile.getAchievements() : new ArrayList<>();
    }

    // Получить разблокированные достижения
    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> allAchievements = getAchievements();
        List<Achievement> unlocked = new ArrayList<>();
        for (Achievement achievement : allAchievements) {
            if (achievement.isUnlocked()) {
                unlocked.add(achievement);
            }
        }
        return unlocked;
    }

    // Разблокировать достижение по имени
    public void unlockAchievement(String achievementName) {
        if (userProfile != null) {
            List<Achievement> achievements = userProfile.getAchievements();
            for (Achievement achievement : achievements) {
                if (achievement.getName().equals(achievementName) && !achievement.isUnlocked()) {
                    achievement.setUnlocked(true);
                    achievement.setUnlockDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                    saveUserProfileToPrefs();
                    showAchievementUnlockedNotification(achievement);
                    break;
                }
            }
        }
    }

    // Проверить и обновить достижения на основе статистики
    public void checkAndUpdateAchievements(UserProgress progress) {
        if (progress == null) return;

        if (progress.getStreak() >= 30) {
            unlockAchievement("Марафонец");
        } else if (progress.getStreak() >= 10) {
            unlockAchievement("Стралец");
        }

        if (progress.getLearnedWords() >= 100) {
            unlockAchievement("Лексiкон");
        }

        if (progress.getGrammarExercises() >= 50) {
            unlockAchievement("Граматык");
        }

        if (progress.getCompletedArticles() >= 10) {
            unlockAchievement("Чытач");
        }
    }

    private void showAchievementUnlockedNotification(Achievement achievement) {
        Toast.makeText(
                appContext,
                "🎉 Дасягненне разблакавана: " + achievement.getName(),
                Toast.LENGTH_LONG
        ).show();
    }

    // Получить прогресс уровня в процентах
    public int getLevelProgress() {
        if (userProfile == null) return 0;
        int xpToNextLevel = userProfile.getXpToNextLevel();
        if (xpToNextLevel == 0) return 0;
        return (int)((float) userProfile.getCurrentLevelXP() / xpToNextLevel * 100);
    }

    // Добавить сессию обучения
    public void addStudySession(String activityType, String activityId, int xpEarned, List<String> learnedWords) {
        if (userProfile != null) {
            StudySession session = new StudySession();
            session.setActivityType(activityType);
            session.setActivityId(activityId);
            session.setXpEarned(xpEarned);
            if (learnedWords != null) {
                session.setLearnedWords(new ArrayList<>(learnedWords));
            }
            session.endSession(xpEarned);
            userProfile.getStudySessions().add(session);
            saveUserProfileToPrefs();
        }
    }

    // Получить статистику за сегодня
    public int getTodayXP() {
        if (userProfile == null) return 0;
        Calendar today = Calendar.getInstance();
        int todayXP = 0;

        for (StudySession session : userProfile.getStudySessions()) {
            Date endTime = session.getEndTime();
            if (endTime != null) {
                Calendar sessionDate = Calendar.getInstance();
                sessionDate.setTime(endTime);
                if (sessionDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        sessionDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                    todayXP += session.getXpEarned();
                }
            }
        }
        return todayXP;
    }

    // Проверить выполнение дневной цели
    public boolean isDailyGoalCompleted() {
        return getTodayXP() >= (userProfile != null ? userProfile.getDailyGoal() : 0);
    }

    // Получить прогресс дневной цели
    public int getDailyGoalProgress() {
        int dailyGoal = userProfile != null ? userProfile.getDailyGoal() : 0;
        if (dailyGoal == 0) return 0;
        return (int)((float) getTodayXP() / dailyGoal * 100);
    }

    // Очистить все данные (для тестирования)
    public void clearAllData() {
        prefs.edit().clear().apply();
        userProfile = null;
        createDefaultUserProfile();
    }

    // Экспорт данных профиля в JSON
    public String exportProfileData() {
        return userProfile != null ? gson.toJson(userProfile) : "";
    }

    // Импорт данных профиля из JSON
    public boolean importProfileData(String jsonData) {
        try {
            if (jsonData != null && !jsonData.isEmpty()) {
                TypeToken<UserProfile> userProfileType = new TypeToken<UserProfile>() {};
                UserProfile importedProfile = gson.fromJson(jsonData, userProfileType.getType());
                if (importedProfile != null) {
                    userProfile = importedProfile;
                    saveUserProfileToPrefs();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}