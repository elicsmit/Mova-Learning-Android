package com.example.mova.ui.data.models;

// NotificationSettings.java - настройки уведомлений

public class NotificationSettings {
    private boolean lessonReminders = true;
    private boolean streakReminders = true;
    private boolean achievementNotifications = true;
    private boolean friendRequests = true;
    private boolean communityUpdates = false;
    private boolean soundEnabled = true;
    private boolean vibrationEnabled = true;

    // Геттеры и сеттеры
    public boolean isLessonReminders() { return lessonReminders; }
    public void setLessonReminders(boolean lessonReminders) { this.lessonReminders = lessonReminders; }

    public boolean isStreakReminders() { return streakReminders; }
    public void setStreakReminders(boolean streakReminders) { this.streakReminders = streakReminders; }

    public boolean isAchievementNotifications() { return achievementNotifications; }
    public void setAchievementNotifications(boolean achievementNotifications) { this.achievementNotifications = achievementNotifications; }

    public boolean isFriendRequests() { return friendRequests; }
    public void setFriendRequests(boolean friendRequests) { this.friendRequests = friendRequests; }

    public boolean isCommunityUpdates() { return communityUpdates; }
    public void setCommunityUpdates(boolean communityUpdates) { this.communityUpdates = communityUpdates; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public boolean isVibrationEnabled() { return vibrationEnabled; }
    public void setVibrationEnabled(boolean vibrationEnabled) { this.vibrationEnabled = vibrationEnabled; }
}