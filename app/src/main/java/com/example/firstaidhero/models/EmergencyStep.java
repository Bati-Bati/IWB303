package com.example.firstaidhero.models;

import java.io.Serializable;

/**
 * Model class representing an action step for an emergency (خطوة إسعافية).
 */
public class EmergencyStep implements Serializable {
    private int stepId;
    private int caseId;
    private int stepNumber;
    private String instructionText;

    public EmergencyStep() {
    }

    public EmergencyStep(int stepId, int caseId, int stepNumber, String instructionText) {
        this.stepId = stepId;
        this.caseId = caseId;
        this.stepNumber = stepNumber;
        this.instructionText = instructionText;
    }

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getInstructionText() {
        return instructionText;
    }

    public void setInstructionText(String instructionText) {
        this.instructionText = instructionText;
    }
}
