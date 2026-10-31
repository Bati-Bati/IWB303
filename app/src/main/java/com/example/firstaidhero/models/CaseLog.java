package com.example.firstaidhero.models;

import java.io.Serializable;

/**
 * Model class representing a documented treated emergency case (حالة معالجة موثقة).
 */
public class CaseLog implements Serializable {
    private int logId;
    private String description;
    private String date;
    private String photo;
    private String procedures;

    public CaseLog() {
    }

    public CaseLog(int logId, String description, String date, String photo, String procedures) {
        this.logId = logId;
        this.description = description;
        this.date = date;
        this.photo = photo;
        this.procedures = procedures;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getProcedures() {
        return procedures;
    }

    public void setProcedures(String procedures) {
        this.procedures = procedures;
    }
}
