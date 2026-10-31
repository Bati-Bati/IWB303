package com.example.firstaidhero;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.CaseLogAdapter;
import com.example.firstaidhero.models.CaseLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CaseLogActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText logDescriptionInput, logDateInput, logProceduresInput, searchLogsInput;
    private Button saveLogButton, logBackButton, pickDateButton;
    private RecyclerView logsRecyclerView;
    private CaseLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_log);

        dbHelper = new DatabaseHelper(this);

        logBackButton = findViewById(R.id.logBackButton);
        logDescriptionInput = findViewById(R.id.logDescriptionInput);
        logDateInput = findViewById(R.id.logDateInput);
        pickDateButton = findViewById(R.id.pickDateButton);
        logProceduresInput = findViewById(R.id.logProceduresInput);
        saveLogButton = findViewById(R.id.saveLogButton);
        searchLogsInput = findViewById(R.id.searchLogsInput);
        logsRecyclerView = findViewById(R.id.logsRecyclerView);

        logBackButton.setOnClickListener(v -> finish());

        // Default date to today
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        logDateInput.setText(today);

        // Date picker setup
        pickDateButton.setOnClickListener(v -> showDatePicker());
        logDateInput.setOnClickListener(v -> showDatePicker());

        // RecyclerView setup
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CaseLogAdapter(this::confirmDeleteLog);
        logsRecyclerView.setAdapter(adapter);

        // Save Log action
        saveLogButton.setOnClickListener(v -> saveNewLog());

        // Instant search for past logs
        searchLogsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterLogs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadAllLogs();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
            logDateInput.setText(selectedDate);
        }, year, month, day);

        dialog.show();
    }

    private void saveNewLog() {
        String description = logDescriptionInput.getText().toString().trim();
        String date = logDateInput.getText().toString().trim();
        String procedures = logProceduresInput.getText().toString().trim();

        if (description.isEmpty()) {
            logDescriptionInput.setError("يرجى إدخال وصف الحالة");
            logDescriptionInput.requestFocus();
            return;
        }

        if (procedures.isEmpty()) {
            logProceduresInput.setError("يرجى إدخال الإجراءات المتخذة");
            logProceduresInput.requestFocus();
            return;
        }

        long id = dbHelper.addCaseLog(description, date, "", procedures);
        if (id != -1) {
            Toast.makeText(this, "تم توثيق الحالة بنجاح", Toast.LENGTH_SHORT).show();
            logDescriptionInput.setText("");
            logProceduresInput.setText("");
            loadAllLogs();
        } else {
            Toast.makeText(this, "حدث خطأ أثناء حفظ التوثيق", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAllLogs() {
        List<CaseLog> logs = dbHelper.getAllCaseLogs();
        adapter.setLogs(logs);
    }

    private void filterLogs(String keyword) {
        if (keyword.trim().isEmpty()) {
            loadAllLogs();
        } else {
            List<CaseLog> filtered = dbHelper.searchCaseLogs(keyword);
            adapter.setLogs(filtered);
        }
    }

    private void confirmDeleteLog(CaseLog log) {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الحذف")
                .setMessage("هل تريد بالتأكيد حذف سجل: \"" + log.getDescription() + "\"؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    dbHelper.deleteCaseLog(log.getLogId());
                    filterLogs(searchLogsInput.getText().toString());
                    Toast.makeText(this, "تم حذف السجل", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
