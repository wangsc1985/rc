package com.wang17.religiouscalendar.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "religiouscalendar.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库后，对数据库的操作
        db.execSQL("create table if not exists setting("
                + "key TEXT PRIMARY KEY,"
                + "value TEXT)");
        db.execSQL("create table if not exists memorialDay("
                + "id TEXT PRIMARY KEY,"
                + "type TEXT,"
                + "relation TEXT,"
                + "month INTEGER,"
                + "day INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更改数据库版本的操作
        if (oldVersion == 1 && newVersion == 2) {
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // 每次成功打开数据库后首先被执行
    }


}
