package com.example.mova.ui.data.models;

// StudyReminders.java - напоминания об учебе

public class StudyReminders {
    private boolean enabled = true;
    private String reminderTime = "19:00";
    private boolean[] daysOfWeek = {true, true, true, true, true, false, false}; // Пн-Пт
    private String customMessage = "Час вучыць беларускую мову!";

    // Геттеры и сеттеры
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public boolean[] getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(boolean[] daysOfWeek) { this.daysOfWeek = daysOfWeek; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
}