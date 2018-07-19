package com.example.kevin.mytodolist1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class DbAdapter {
    public static final String KEY_ID = "_id";
    public static final String KEY_CREATE_TIME = "create_time";
    public static final String KEY_SCHEDULED_TIME = "scheduled_time";
    public static final String KEY_COMPLETE_TIME = "complete_time";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_COLOR = "color";
    private static final String DATABASE_NAME = "MyDatabase";
    private static final String TABLE_NAME = "todo";
    private static final int DATABASE_VERSION = 1;
    private static final String CLASSNAME="DbAdapter";
    private Context mCtx;
    private DbHelper mDbHelper;
    private SQLiteDatabase mdb;
//    private Intent intent;
//    private ContentValues values;

    public DbAdapter(Context context) {
        this.mCtx =context;
        open();
    }


    public void open()
    {
        Log.d(CLASSNAME+":open()","Begin");
        mDbHelper = new DbHelper(mCtx);
        mdb = mDbHelper.getWritableDatabase();
        Log.d(CLASSNAME+":open()", "mdb="+mdb.toString());
    }

    public void close()
    {
        Log.d(CLASSNAME+":close()","Begin");
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        Log.d(CLASSNAME+":close()", "End");
    }
    public long createTodo(String scheduled_time, String notes, String color)
    {
        long ret=0;
        Log.d("DbAdapter","createTodo():Begin");
        ContentValues values=new ContentValues();
        values.put(KEY_SCHEDULED_TIME, scheduled_time);
        values.put(KEY_NOTES, notes);
        values.put(KEY_COLOR, color);
        try
        {
            ret=mdb.insert(TABLE_NAME,null,values);
            Toast.makeText(mCtx, "新增成功!", Toast.LENGTH_SHORT).show();
            Log.d("DbAdapter", "createContact():ret="+ret);
        }
        catch (Exception e)
        {
            Log.d("DbAdapter","createContact():mdb.insert() error");
            e.printStackTrace();
        }
        Log.d("DbAdapter", "createContact():End, ret="+ret);
        return ret;
    }

    public long updateToDo(int id, String scheduled_time, String complete_time, String notes, String color)
    {
        Log.d("DbAdapter","updateToDo():Begin");
        long ret=0;
        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULED_TIME, scheduled_time);
        values.put(KEY_COMPLETE_TIME, complete_time);
        values.put(KEY_NOTES, notes);
        values.put(KEY_COLOR, color);
        try
        {
            ret=mdb.update(TABLE_NAME, values, "_id="+id,null);
            Toast.makeText(mCtx, "更新成功", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.d("DbAdapter", "updateToDo():mdb.update() error");
            e.printStackTrace();
        }
        Toast.makeText(mCtx,"更新成功!", Toast.LENGTH_SHORT).show();
        Log.d("DbAdapter", "updateToDo():End, ret="+ret);
        return ret;
    }

    public boolean dropTodo(int id)
    {
        int count=0;
        Log.d("DbAdapter", "dropTodo():Begin, id="+id);
        String[] args = {Integer.toString(id)};
        try
        {
            count=mdb.delete(TABLE_NAME, "_id="+id, null);
            Toast.makeText(mCtx, "刪除"+count+"筆資料", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.d("DbAdapter","dropTodo():mdb.delete() error");
            e.printStackTrace();
        }
        Log.d("DbAdapter","dropTodo():End");
        return true;
    }

    public Cursor queryById(int id)
    {
        Log.d("DbAdapter","queryById():Begin");
        Cursor mCursor = null;
        try
        {
            mCursor = mdb.query(true, TABLE_NAME, new String[] {KEY_ID, KEY_CREATE_TIME, KEY_SCHEDULED_TIME, KEY_COMPLETE_TIME, KEY_NOTES, KEY_COLOR},
                    KEY_ID + "=" + id,null,
                    null, null, "scheduled_time", null);
            if (mCursor!=null)
            {
                Log.d("DbAdapter","queryById():Count="+mCursor.getCount());
                mCursor.moveToFirst();
            }
            else
                Log.d("DbAdapter", "queryById():mCursor=null");
        }
        catch (Exception e)
        {
            Log.d("DbAdapter", "queryById():mdb.query() error");
            e.printStackTrace();
        }
        Log.d("DbAdapter", "queryById():End");
        return mCursor;
    }

    public Cursor listToDo()
    {
        Log.d("DbAdapter","listToDo():Begin");
        Cursor mCursor = null;
        try
        {
            mCursor = mdb.query(true, TABLE_NAME, new String[] {KEY_ID, KEY_SCHEDULED_TIME, KEY_COMPLETE_TIME, KEY_NOTES, KEY_COLOR},
                    null,null,null, null, "scheduled_time", null);
            if (mCursor!=null)
                mCursor.moveToFirst();

        }
        catch (Exception e)
        {
            Log.d("DbAdapter","listToDo():mdb.query() error");
            e.printStackTrace();
        }
        Log.d("DbAdapter","listToDo():End");
        return mCursor;
    }

}
