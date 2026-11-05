package com.example.firstaidhero;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.EmergencyAdapter;
import com.example.firstaidhero.models.Emergency;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private EditText searchEditText;
    private RecyclerView emergenciesRecyclerView;
    private Button btnQuickDialer, btnCaseLogs, btnReports;
    private EmergencyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        searchEditText = findViewById(R.id.searchEditText);
        emergenciesRecyclerView = findViewById(R.id.emergenciesRecyclerView);
        btnQuickDialer = findViewById(R.id.btnQuickDialer);
        btnCaseLogs = findViewById(R.id.btnCaseLogs);
        btnReports = findViewById(R.id.btnReports);

        // التنقل لشاشة أرقام الطوارئ
        btnQuickDialer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyDialerActivity.class);
            startActivity(intent);
        });

        // التنقل لشاشة توثيق الحالات المعالجة
        btnCaseLogs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CaseLogActivity.class);
            startActivity(intent);
        });

        // التنقل لشاشة الإحصائيات والتقارير
        btnReports.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
            startActivity(intent);
        });

        // إعداد الـ RecyclerView
        emergenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyAdapter(emergency -> {
            Intent intent = new Intent(MainActivity.this, ActionStepsActivity.class);
            intent.putExtra("CASE_ID", emergency.getCaseId());
            startActivity(intent);
        });
        emergenciesRecyclerView.setAdapter(adapter);

        // تحميل الحالات الأولية
        loadEmergencies("");

        // البحث الفوري التفاعلي (Instant Search)
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadEmergencies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadEmergencies(String keyword) {
        List<Emergency> emergencies;
        if (keyword.trim().isEmpty()) {
            emergencies = dbHelper.getAllEmergencies();
        } else {
            emergencies = dbHelper.searchEmergencies(keyword);
        }
        adapter.setEmergencies(emergencies);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
