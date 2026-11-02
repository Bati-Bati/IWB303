package com.example.firstaidhero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstaidhero.models.Emergency;
import com.example.firstaidhero.models.EmergencyStep;

import java.util.List;

public class ActionStepsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView titleTextView, dangerLevelTextView, symptomsTextView;
    private LinearLayout stepsContainer;
    private Button backButton, callEmergencyInStepsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_steps);

        dbHelper = new DatabaseHelper(this);

        titleTextView = findViewById(R.id.titleTextView);
        dangerLevelTextView = findViewById(R.id.dangerLevelTextView);
        symptomsTextView = findViewById(R.id.symptomsTextView);
        stepsContainer = findViewById(R.id.stepsContainer);
        backButton = findViewById(R.id.backButton);
        callEmergencyInStepsButton = findViewById(R.id.callEmergencyInStepsButton);

        backButton.setOnClickListener(v -> finish());

        // Dynamically fetch emergency contact number from DB
        List<EmergencyContact> contacts = dbHelper.getAllContacts();
        String emergencyNumber = "101";
        String contactName = "الإسعاف";
        if (contacts != null && !contacts.isEmpty()) {
            emergencyNumber = contacts.get(0).getPhoneNumber();
            contactName = contacts.get(0).getContactName();
        }

        final String finalEmergencyNumber = emergencyNumber;
        callEmergencyInStepsButton.setText("اتصال بـ " + contactName + " (" + finalEmergencyNumber + ")");
        callEmergencyInStepsButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + finalEmergencyNumber));
            startActivity(callIntent);
        });

        int caseId = getIntent().getIntExtra("CASE_ID", 1);
        loadEmergencyDetails(caseId);
        loadActionSteps(caseId);
    }

    private void loadEmergencyDetails(int caseId) {
        Emergency emergency = dbHelper.getEmergencyById(caseId);
        if (emergency != null) {
            titleTextView.setText(emergency.getTitle());
            dangerLevelTextView.setText("مستوى الخطورة: " + emergency.getDangerLevel());
            symptomsTextView.setText("الأعراض والعلامات:\n" + emergency.getSymptoms());

            if (emergency.isCritical()) {
                dangerLevelTextView.setBackgroundResource(R.drawable.bg_danger_badge);
            } else {
                dangerLevelTextView.setBackgroundColor(0xFFFFA000);
            }
        }
    }

    private void loadActionSteps(int caseId) {
        List<EmergencyStep> steps = dbHelper.getStepsForCase(caseId);
        stepsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < steps.size(); i++) {
            EmergencyStep step = steps.get(i);
            View stepView = inflater.inflate(R.layout.item_step, stepsContainer, false);

            TextView numberText = stepView.findViewById(R.id.stepNumberText);
            TextView instructionText = stepView.findViewById(R.id.stepInstructionText);

            int num = step.getStepNumber() > 0 ? step.getStepNumber() : (i + 1);
            numberText.setText(String.valueOf(num));
            instructionText.setText(step.getInstructionText());

            stepsContainer.addView(stepView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}