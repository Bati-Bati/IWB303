package com.example.firstaidhero;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "first_aid_hero.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. إنشاء الجداول الأربعة المطلوبة
        db.execSQL("CREATE TABLE emergencies (case_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, danger_level TEXT, symptoms TEXT);");
        db.execSQL("CREATE TABLE steps (step_id INTEGER PRIMARY KEY AUTOINCREMENT, case_id INTEGER, step_number INTEGER, instruction_text TEXT);");
        db.execSQL("CREATE TABLE custom_contacts (contact_id INTEGER PRIMARY KEY AUTOINCREMENT, contact_name TEXT, phone_number TEXT);");
        db.execSQL("CREATE TABLE case_logs (log_id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, date TEXT, photo TEXT, procedures TEXT);");

        // 2. إدخال بيانات أولية (سيناريو لدغات الأفاعي والاختناق) للاختبار الفوري
        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('لدغات الأفاعي والحشرات', 'حرِج', 'ألم شديد، تورم، دوخة، صعوبة في التنفس.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 1, 'ثبّت الطرف المصاب وحافظ على هدوء المصاب لمنع انتشار السم.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 2, 'انزع أي خواتم أو ساعات أو ملابس ضيقة قرب موقع اللدغة.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (1, 3, 'لا تحاول مص السم أو جرح مكان اللدغة أبداً.');");

        db.execSQL("INSERT INTO emergencies (title, danger_level, symptoms) VALUES ('الاختناق', 'حرِج', 'عدم القدرة على الكلام، تحول الشفاه للون الأزرق.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 1, 'قم بتشجيع المصاب على السعال بقوة.');");
        db.execSQL("INSERT INTO steps (case_id, step_number, instruction_text) VALUES (2, 2, 'طبق ضغطات البطن (مناورة هيمليخ) خلف المصاب.');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS emergencies");
        db.execSQL("DROP TABLE IF EXISTS steps");
        db.execSQL("DROP TABLE IF EXISTS custom_contacts");
        db.execSQL("DROP TABLE IF EXISTS case_logs");
        onCreate(db);
    }

    // استعلام البحث الفوري المطلوب (Instant Search) باستخدام LIKE
    public Cursor searchEmergencies(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM emergencies WHERE title LIKE ? OR symptoms LIKE ?";
        return db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }

    // استرجاع كل الحالات
    public Cursor getAllEmergencies() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM emergencies", null);
    }

    // استرجاع خطوات الإسعاف الخاصة بحالة معينة
    public Cursor getStepsForCase(int caseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM steps WHERE case_id = ? ORDER BY step_number ASC", new String[]{String.valueOf(caseId)});
    }
}