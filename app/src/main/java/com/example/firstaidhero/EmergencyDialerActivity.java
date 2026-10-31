package com.example.firstaidhero;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.adapters.ContactAdapter;
import com.example.firstaidhero.models.EmergencyContact;

import java.util.List;

public class EmergencyDialerActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText contactNameEditText, contactPhoneEditText;
    private Button saveContactButton, dialerBackButton;
    private RecyclerView contactsRecyclerView;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_dialer);

        dbHelper = new DatabaseHelper(this);

        dialerBackButton = findViewById(R.id.dialerBackButton);
        contactNameEditText = findViewById(R.id.contactNameEditText);
        contactPhoneEditText = findViewById(R.id.contactPhoneEditText);
        saveContactButton = findViewById(R.id.saveContactButton);
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);

        dialerBackButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(new ContactAdapter.OnContactActionListener() {
            @Override
            public void onCall(EmergencyContact contact) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
                startActivity(dialIntent);
            }

            @Override
            public void onDelete(EmergencyContact contact) {
                new AlertDialog.Builder(EmergencyDialerActivity.this)
                        .setTitle("تأكيد الحذف")
                        .setMessage("هل أنت متأكد من حذف \"" + contact.getContactName() + "\"؟")
                        .setPositiveButton("حذف", (dialog, which) -> {
                            dbHelper.deleteContact(contact.getContactId());
                            loadContacts();
                            Toast.makeText(EmergencyDialerActivity.this, "تم حذف جهة الاتصال", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("إلغاء", null)
                        .show();
            }
        });
        contactsRecyclerView.setAdapter(adapter);

        saveContactButton.setOnClickListener(v -> {
            String name = contactNameEditText.getText().toString().trim();
            String phone = contactPhoneEditText.getText().toString().trim();

            if (name.isEmpty()) {
                contactNameEditText.setError("يرجى إدخال اسم جهة الاتصال");
                contactNameEditText.requestFocus();
                return;
            }

            if (phone.isEmpty()) {
                contactPhoneEditText.setError("يرجى إدخال رقم الهاتف");
                contactPhoneEditText.requestFocus();
                return;
            }

            long result = dbHelper.addContact(name, phone);
            if (result != -1) {
                Toast.makeText(this, "تم حفظ جهة الاتصال بنجاح", Toast.LENGTH_SHORT).show();
                contactNameEditText.setText("");
                contactPhoneEditText.setText("");
                loadContacts();
            } else {
                Toast.makeText(this, "حدث خطأ أثناء الحفظ", Toast.LENGTH_SHORT).show();
            }
        });

        loadContacts();
    }

    private void loadContacts() {
        List<EmergencyContact> contacts = dbHelper.getAllContacts();
        adapter.setContacts(contacts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
