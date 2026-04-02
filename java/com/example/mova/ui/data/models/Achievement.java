package com.example.mova.ui.data.models;

public class Achievement {
    private String id;
    private String name;
    private String description;
    private int iconRes;
    private boolean unlocked;
    private int progress;
    private int target;
    private String unlockDate;

    public Achievement(String name, String description, int iconRes, boolean unlocked) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.iconRes = iconRes;
        this.unlocked = unlocked;
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }
    public String getUnlockDate() { return unlockDate; }
    public void setUnlockDate(String unlockDate) { this.unlockDate = unlockDate; }
}
