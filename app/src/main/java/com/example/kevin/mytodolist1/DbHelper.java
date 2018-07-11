package com.example.kevin.mytodolist1;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String KEY_ID = "_id";
    public static final String KEY_CREATE_TIME = "create_time";
    public static final String KEY_SCHEDULED_TIME = "scheduled_time";
    public static final String KEY_COMPLETE_TIME = "complete_time";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_COLOR = "color";
    private static final String DATABASE_NAME = "MyDatabase";
    private static final String TABLE_NAME = "todo";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String DATABASE_CREATE = "create table if not exists " + TABLE_NAME + " (" +
                KEY_ID + " integer primary key autoincrement," +
                KEY_CREATE_TIME + " datetime default CURRENT_TIMESTAMP," +
                KEY_SCHEDULED_TIME + " datetime," +
                KEY_COMPLETE_TIME + " datetime," +
                KEY_NOTES + " text," +
                KEY_COLOR + " integer);";
        db.execSQL("drop table "+TABLE_NAME+";");
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);

    }
}