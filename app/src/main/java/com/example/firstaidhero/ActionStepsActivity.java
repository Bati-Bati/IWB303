package com.example.firstaidhero;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ActionStepsActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    TextView titleTextView, dangerLevelTextView, symptomsTextView;
    ListView stepsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_steps);

        dbHelper = new DatabaseHelper(this);

        titleTextView = findViewById(R.id.titleTextView);
        dangerLevelTextView = findViewById(R.id.dangerLevelTextView);
        symptomsTextView = findViewById(R.id.symptomsTextView);
        stepsListView = findViewById(R.id.stepsListView);

        // استقبال رقم الحالة (case_id) المرسل من الشاشة الرئيسية
        int caseId = getIntent().getIntExtra("CASE_ID", 1);

        // جلب بيانات الحالة وعرضها
        Cursor cursor = dbHelper.getAllEmergencies();
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndexOrThrow("case_id")) == caseId) {
                titleTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                dangerLevelTextView.setText("مستوى الخطورة: " + cursor.getString(cursor.getColumnIndexOrThrow("danger_level")));
                symptomsTextView.setText("الأعراض: " + cursor.getString(cursor.getColumnIndexOrThrow("symptoms")));
                break;
            }
        }
        cursor.close();

        // جلب وترتيب الخطوات الإسعافية الخاصة بالحالة
        Cursor stepsCursor = dbHelper.getStepsForCase(caseId);
        String[] fromColumns = {"instruction_text"};
        int[] toViews = {android.R.id.text1};

        SimpleCursorAdapter stepsAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                stepsCursor,
                fromColumns,
                toViews,
                0
        );

        stepsListView.setAdapter(stepsAdapter);
    }
}