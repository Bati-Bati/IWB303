package com.example.firstaidhero;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstaidhero.models.CaseLog;

import java.util.List;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView statTotalEmergencies, statTotalLogs, statCriticalCount, statModerateCount, recentLogsSummaryText;
    private Button reportsBackButton, openCaseLogsFromReportsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dbHelper = new DatabaseHelper(this);

        reportsBackButton = findViewById(R.id.reportsBackButton);
        statTotalEmergencies = findViewById(R.id.statTotalEmergencies);
        statTotalLogs = findViewById(R.id.statTotalLogs);
        statCriticalCount = findViewById(R.id.statCriticalCount);
        statModerateCount = findViewById(R.id.statModerateCount);
        recentLogsSummaryText = findViewById(R.id.recentLogsSummaryText);
        openCaseLogsFromReportsButton = findViewById(R.id.openCaseLogsFromReportsButton);

        reportsBackButton.setOnClickListener(v -> finish());

        openCaseLogsFromReportsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CaseLogActivity.class);
            startActivity(intent);
        });

        loadReportData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReportData();
    }

    private void loadReportData() {
        // 1. إجمالي الحالات
        int totalEmergencies = dbHelper.getTotalEmergenciesCount();
        statTotalEmergencies.setText(String.valueOf(totalEmergencies));

        // 2. إجمالي الحالات المعالجة
        int totalLogs = dbHelper.getTotalLogsCount();
        statTotalLogs.setText(String.valueOf(totalLogs));

        // 3. تصنيف الحالات حسب الخطورة عبر استعلام المجاميع التكرارية (GROUP BY)
        Map<String, Integer> counts = dbHelper.getDangerLevelCounts();

        int critical = 0;
        int moderate = 0;

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                if (key.contains("حرج") || key.contains("حرِج")) {
                    critical += entry.getValue();
                } else if (key.contains("متوسط")) {
                    moderate += entry.getValue();
                }
            }
        }

        statCriticalCount.setText(String.valueOf(critical));
        statModerateCount.setText(String.valueOf(moderate));

        // 4. ملخص أحدث الحالات المعالجة
        List<CaseLog> recentLogs = dbHelper.getAllCaseLogs();
        if (recentLogs.isEmpty()) {
            recentLogsSummaryText.setText("لم يتم توثيق أي حالة إسعافية بعد.\nيمكنك إضافة أول حالة عبر الضغط على الزر أدناه.");
        } else {
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(recentLogs.size(), 3);
            for (int i = 0; i < limit; i++) {
                CaseLog log = recentLogs.get(i);
                sb.append("• [").append(log.getDate()).append("] ").append(log.getDescription()).append("\n");
                sb.append("  الإجراء: ").append(log.getProcedures()).append("\n\n");
            }
            recentLogsSummaryText.setText(sb.toString().trim());
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
