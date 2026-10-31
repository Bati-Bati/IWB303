package com.example.firstaidhero.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "first_aid_hero.db";
    private static final int DATABASE_VERSION = 1;

    // أسماء الجداول المطلوبة في الوظيفة
    public static final String TABLE_EMERGENCIES = "emergencies";
    public static final String TABLE_STEPS = "steps";
    public static final String TABLE_CONTACTS = "custom_contacts";
    public static final String TABLE_LOGS = "case_logs";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. جدول الحالات الإسعافية
        String CREATE_EMERGENCIES_TABLE = "CREATE TABLE " + TABLE_EMERGENCIES + "("
                + "case_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT,"
                + "danger_level TEXT,"
                + "symptoms TEXT" + ")";
        db.execSQL(CREATE_EMERGENCIES_TABLE);

        // 2. جدول خطوات الإسعاف (علاقة One-to-Many مع جدول الحالات)
        String CREATE_STEPS_TABLE = "CREATE TABLE " + TABLE_STEPS + "("
                + "step_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "case_id INTEGER,"
                + "step_number INTEGER,"
                + "instruction_text TEXT,"
                + "FOREIGN KEY(case_id) REFERENCES " + TABLE_EMERGENCIES + "(case_id))";
        db.execSQL(CREATE_STEPS_TABLE);

        // 3. جدول أرقام الطوارئ المخصصة
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + "contact_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "contact_name TEXT,"
                + "phone_number TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        // 4. جدول إدخال وتوثيق الحالات المعالجة والتقارير
        String CREATE_LOGS_TABLE = "CREATE TABLE " + TABLE_LOGS + "("
                + "log_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "description TEXT,"
                + "date TEXT,"
                + "photo TEXT,"
                + "procedures TEXT" + ")";
        db.execSQL(CREATE_LOGS_TABLE);

        // إدخال بيانات أولية (مثال سيناريو لدغات الأفاعي) لضمان سرعة الاختبار الفوري
        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        // إدخال حالة لدغات الأفاعي
        db.execSQL("INSERT INTO " + TABLE_EMERGENCIES + " (title, danger_level, symptoms) VALUES ('لدغات الأفاعي والحشرات', 'حرِج', 'ألم شديد، تورم في مكان اللدغة، دوخة، صعوبة في التنفس.');");

        // إدخال الخطوات الخاصة بالحالة (case_id = 1)
        db.execSQL("INSERT INTO " + TABLE_STEPS + " (case_id, step_number, instruction_text) VALUES (1, 1, 'ثبّت الطرف المصاب وحافظ على هدوء المصاب لمنع انتشار السم.');");
        db.execSQL("INSERT INTO " + TABLE_STEPS + " (case_id, step_number, instruction_text) VALUES (1, 2, 'انزع أي خواتم أو ساعات أو ملابس ضيقة قرب موقع اللدغة قبل بدء التورم.');");
        db.execSQL("INSERT INTO " + TABLE_STEPS + " (case_id, step_number, instruction_text) VALUES (1, 3, 'لا تحاول مص السم أو جرح مكان اللدغة أبداً، وتوجه فوراً لأقرب طوارئ.');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMERGENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

    // استعلام البحث الفوري المطلوب (يتحقق من العنوان أو الأعراض باستخدام LIKE)
    public Cursor searchEmergencies(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EMERGENCIES + " WHERE title LIKE ? OR symptoms LIKE ?";
        return db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
    }
}
