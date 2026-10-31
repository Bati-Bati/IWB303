package com.example.firstaidhero.models;

import java.io.Serializable;

/**
 * Model class representing an emergency contact (رقم طوارئ).
 */
public class EmergencyContact implements Serializable {
    private int contactId;
    private String contactName;
    private String phoneNumber;

    public EmergencyContact() {
    }

    public EmergencyContact(int contactId, String contactName, String phoneNumber) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
