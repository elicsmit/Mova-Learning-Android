package com.example.mova.ui.data.models;

// StudySession.java - сессия обучения

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudySession {
    private String id;
    private Date startTime;
    private Date endTime;
    private int xpEarned;
    private String activityType; // "lesson", "article", "training", "grammar"
    private String activityId;
    private List<String> learnedWords = new ArrayList<>();

    public StudySession() {
        this.id = java.util.UUID.randomUUID().toString();
        this.startTime = new Date();
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
    public List<String> getLearnedWords() { return learnedWords; }
    public void setLearnedWords(List<String> learnedWords) { this.learnedWords = learnedWords; }

    public void endSession(int xp) {
        this.endTime = new Date();
        this.xpEarned = xp;
    }
}