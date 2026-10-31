package com.example.firstaidhero;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText searchEditText;
    ListView emergenciesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        searchEditText = findViewById(R.id.searchEditText);
        emergenciesListView = findViewById(R.id.emergenciesListView);

        // عرض كل الحالات عند فتح التطبيق
        loadEmergencies("");

        // تفعيل البحث الفوري (Instant Search) أثناء الكتابة في شريط البحث
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

        // الانتقال لشاشة الخطوات عند النقر على أي حالة
        emergenciesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ActionStepsActivity.class);
                intent.putExtra("CASE_ID", (int) id);
                startActivity(intent);
            }
        });
    }

    private void loadEmergencies(String keyword) {
        Cursor cursor = keyword.isEmpty() ? dbHelper.getAllEmergencies() : dbHelper.searchEmergencies(keyword);

        String[] fromColumns = {"title", "symptoms"};
        int[] toViews = {android.R.id.text1, android.R.id.text2};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                fromColumns,
                toViews,
                0
        );

        emergenciesListView.setAdapter(adapter);
    }
}
