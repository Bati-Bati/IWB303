package com.example.firstaidhero;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.firstaidhero.models.CaseLog;
import com.example.firstaidhero.models.Emergency;
import com.example.firstaidhero.models.EmergencyContact;
import com.example.firstaidhero.models.EmergencyStep;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Robust DatabaseHelper responsible for:
 * 1. Safely extracting and initializing pre-populated SQLite DB from assets/databases/
 * 2. Providing clean, strongly-typed CRUD APIs for domain models
 * 3. Providing aggregate statistical reporting queries
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "first_aid_hero.db";
    private static final int DATABASE_VERSION = 2;

    // Database Tables
    public static final String TABLE_EMERGENCIES = "emergencies";
    public static final String TABLE_STEPS = "steps";
    public static final String TABLE_CONTACTS = "custom_contacts";
    public static final String TABLE_CASE_LOGS = "case_logs";

    // Columns: Emergencies
    public static final String COL_CASE_ID = "case_id";
    public static final String COL_TITLE = "title";
    public static final String COL_DANGER_LEVEL = "danger_level";
    public static final String COL_SYMPTOMS = "symptoms";

    // Columns: Steps
    public static final String COL_STEP_ID = "step_id";
    public static final String COL_STEP_CASE_ID = "case_id";
    public static final String COL_STEP_NUMBER = "step_number";
    public static final String COL_INSTRUCTION_TEXT = "instruction_text";

    // Columns: Contacts
    public static final String COL_CONTACT_ID = "contact_id";
    public static final String COL_CONTACT_NAME = "contact_name";
    public static final String COL_PHONE_NUMBER = "phone_number";

    // Columns: Case Logs
    public static final String COL_LOG_ID = "log_id";
    public static final String COL_LOG_DESCRIPTION = "description";
    public static final String COL_LOG_DATE = "date";
    public static final String COL_LOG_PHOTO = "photo";
    public static final String COL_LOG_PROCEDURES = "procedures";

    private final Context context;
    private final String dbPath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        initializeDatabaseFromAssets();
    }

    /**
     * Checks if the SQLite database exists on disk; if not, copies the pre-populated
     * asset database binary from assets/databases/first_aid_hero.db.
     */
    private void initializeDatabaseFromAssets() {
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (InputStream input = context.getAssets().open("databases/" + DATABASE_NAME);
                 OutputStream output = new FileOutputStream(dbFile)) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
                output.flush();
                Log.i(TAG, "Pre-populated database successfully copied from assets to " + dbPath);
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy pre-populated database from assets, falling back to onCreate", e);
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Fallback schema creation if asset copy fails
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EMERGENCIES + " ("
                + COL_CASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_DANGER_LEVEL + " TEXT NOT NULL, "
                + COL_SYMPTOMS + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STEPS + " ("
                + COL_STEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_STEP_CASE_ID + " INTEGER NOT NULL, "
                + COL_STEP_NUMBER + " INTEGER NOT NULL, "
                + COL_INSTRUCTION_TEXT + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + COL_STEP_CASE_ID + ") REFERENCES " + TABLE_EMERGENCIES + " (" + COL_CASE_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + " ("
                + COL_CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CONTACT_NAME + " TEXT NOT NULL, "
                + COL_PHONE_NUMBER + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CASE_LOGS + " ("
                + COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOG_DESCRIPTION + " TEXT NOT NULL, "
                + COL_LOG_DATE + " TEXT NOT NULL, "
                + COL_LOG_PHOTO + " TEXT, "
                + COL_LOG_PROCEDURES + " TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMERGENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CASE_LOGS);
        onCreate(db);
    }

    // ==========================================
    // EMERGENCIES (الحالات الإسعافية)
    // ==========================================

    public List<Emergency> getAllEmergencies() {
        List<Emergency> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_CASE_ID + ", " + COL_TITLE + ", " + COL_DANGER_LEVEL + ", " + COL_SYMPTOMS 
                + " FROM " + TABLE_EMERGENCIES + " ORDER BY " + COL_CASE_ID + " ASC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                list.add(mapCursorToEmergency(cursor));
            }
        }
        return list;
    }

    public List<Emergency> searchEmergencies(String keyword) {
        List<Emergency> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_CASE_ID + ", " + COL_TITLE + ", " + COL_DANGER_LEVEL + ", " + COL_SYMPTOMS 
                + " FROM " + TABLE_EMERGENCIES 
                + " WHERE " + COL_TITLE + " LIKE ? OR " + COL_SYMPTOMS + " LIKE ? ORDER BY " + COL_CASE_ID + " ASC";

        String pattern = "%" + keyword.trim() + "%";
        try (Cursor cursor = db.rawQuery(query, new String[]{pattern, pattern})) {
            while (cursor.moveToNext()) {
                list.add(mapCursorToEmergency(cursor));
            }
        }
        return list;
    }

    public Emergency getEmergencyById(int caseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_CASE_ID + ", " + COL_TITLE + ", " + COL_DANGER_LEVEL + ", " + COL_SYMPTOMS 
                + " FROM " + TABLE_EMERGENCIES + " WHERE " + COL_CASE_ID + " = ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(caseId)})) {
            if (cursor.moveToFirst()) {
                return mapCursorToEmergency(cursor);
            }
        }
        return null;
    }

    private Emergency mapCursorToEmergency(Cursor cursor) {
        return new Emergency(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_CASE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DANGER_LEVEL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_SYMPTOMS))
        );
    }

    // ==========================================
    // ACTION STEPS (خطوات الإسعاف)
    // ==========================================

    public List<EmergencyStep> getStepsForCase(int caseId) {
        List<EmergencyStep> steps = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_STEP_ID + ", " + COL_STEP_CASE_ID + ", " + COL_STEP_NUMBER + ", " + COL_INSTRUCTION_TEXT
                + " FROM " + TABLE_STEPS + " WHERE " + COL_STEP_CASE_ID + " = ? ORDER BY " + COL_STEP_NUMBER + " ASC";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(caseId)})) {
            while (cursor.moveToNext()) {
                steps.add(new EmergencyStep(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEP_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEP_CASE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEP_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTION_TEXT))
                ));
            }
        }
        return steps;
    }

    // ==========================================
    // EMERGENCY CONTACTS (أرقام الطوارئ المخصصة)
    // ==========================================

    public List<EmergencyContact> getAllContacts() {
        List<EmergencyContact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_CONTACT_ID + ", " + COL_CONTACT_NAME + ", " + COL_PHONE_NUMBER 
                + " FROM " + TABLE_CONTACTS + " ORDER BY " + COL_CONTACT_ID + " ASC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                contacts.add(new EmergencyContact(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_CONTACT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE_NUMBER))
                ));
            }
        }
        return contacts;
    }

    public long addContact(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CONTACT_NAME, name);
        values.put(COL_PHONE_NUMBER, phone);
        return db.insert(TABLE_CONTACTS, null, values);
    }

    public int updateContact(int contactId, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CONTACT_NAME, name);
        values.put(COL_PHONE_NUMBER, phone);
        return db.update(TABLE_CONTACTS, values, COL_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
    }

    public int deleteContact(int contactId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CONTACTS, COL_CONTACT_ID + " = ?", new String[]{String.valueOf(contactId)});
    }

    // ==========================================
    // TREATED CASE LOGS (توثيق الحالات المعالجة)
    // ==========================================

    public List<CaseLog> getAllCaseLogs() {
        List<CaseLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_LOG_ID + ", " + COL_LOG_DESCRIPTION + ", " + COL_LOG_DATE + ", " + COL_LOG_PHOTO + ", " + COL_LOG_PROCEDURES
                + " FROM " + TABLE_CASE_LOGS + " ORDER BY " + COL_LOG_DATE + " DESC, " + COL_LOG_ID + " DESC";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                logs.add(mapCursorToCaseLog(cursor));
            }
        }
        return logs;
    }

    public List<CaseLog> searchCaseLogs(String keyword) {
        List<CaseLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_LOG_ID + ", " + COL_LOG_DESCRIPTION + ", " + COL_LOG_DATE + ", " + COL_LOG_PHOTO + ", " + COL_LOG_PROCEDURES
                + " FROM " + TABLE_CASE_LOGS 
                + " WHERE " + COL_LOG_DESCRIPTION + " LIKE ? OR " + COL_LOG_DATE + " LIKE ? OR " + COL_LOG_PROCEDURES + " LIKE ?"
                + " ORDER BY " + COL_LOG_DATE + " DESC";

        String pattern = "%" + keyword.trim() + "%";
        try (Cursor cursor = db.rawQuery(query, new String[]{pattern, pattern, pattern})) {
            while (cursor.moveToNext()) {
                logs.add(mapCursorToCaseLog(cursor));
            }
        }
        return logs;
    }

    public long addCaseLog(String description, String date, String photo, String procedures) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOG_DESCRIPTION, description);
        values.put(COL_LOG_DATE, date);
        values.put(COL_LOG_PHOTO, photo);
        values.put(COL_LOG_PROCEDURES, procedures);
        return db.insert(TABLE_CASE_LOGS, null, values);
    }

    public int deleteCaseLog(int logId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CASE_LOGS, COL_LOG_ID + " = ?", new String[]{String.valueOf(logId)});
    }

    private CaseLog mapCursorToCaseLog(Cursor cursor) {
        return new CaseLog(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_PHOTO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_PROCEDURES))
        );
    }

    // ==========================================
    // STATISTICAL & AGGREGATE QUERIES (التقارير والإحصائيات)
    // ==========================================

    /**
     * Executes aggregate GROUP BY query to count cases per danger level.
     * Example SQL: SELECT danger_level, COUNT(*) FROM emergencies GROUP BY danger_level
     */
    public Map<String, Integer> getDangerLevelCounts() {
        Map<String, Integer> counts = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_DANGER_LEVEL + ", COUNT(*) AS total FROM " + TABLE_EMERGENCIES + " GROUP BY " + COL_DANGER_LEVEL;

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                String level = cursor.getString(0);
                int count = cursor.getInt(1);
                counts.put(level, count);
            }
        }
        return counts;
    }

    public int getTotalEmergenciesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EMERGENCIES, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public int getTotalLogsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CASE_LOGS, null)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }
}