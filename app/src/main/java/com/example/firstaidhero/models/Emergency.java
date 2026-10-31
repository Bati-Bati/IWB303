package com.example.firstaidhero.models;

import java.io.Serializable;

/**
 * Model class representing an emergency scenario (حالة إسعافية).
 */
public class Emergency implements Serializable {
    private int caseId;
    private String title;
    private String dangerLevel;
    private String symptoms;

    public Emergency() {
    }

    public Emergency(int caseId, String title, String dangerLevel, String symptoms) {
        this.caseId = caseId;
        this.title = title;
        this.dangerLevel = dangerLevel;
        this.symptoms = symptoms;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(String dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public boolean isCritical() {
        return "حرِج".equalsIgnoreCase(dangerLevel) || "حرج".equalsIgnoreCase(dangerLevel);
    }
}
