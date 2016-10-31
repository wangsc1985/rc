package com.wang17.religiouscalendar.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wang17.religiouscalendar.emnu.MDrelation;
import com.wang17.religiouscalendar.emnu.MDtype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;

    public DataContext(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void addMemorialDay(MemorialDay memorialDay) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("id", memorialDay.getId().toString());
        values.put("type", memorialDay.getType().toInt());
        values.put("relation", memorialDay.getRelation().toInt());
        values.put("month", memorialDay.getLunarDate().getMonth());
        values.put("day", memorialDay.getLunarDate().getDay());

        //调用方法插入数据
        db.insert("memorialDay", "id", values);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public MemorialDay getMemorialDay(UUID id) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("memorialDay", null, "id=?", new String[]{id.toString()}, null, null, null);
        //判断游标是否为空
        if (cursor.moveToNext()) {
            MemorialDay model = new MemorialDay();
            model.setId(id);
            model.setType(MDtype.fromInt(cursor.getInt(1)));
            model.setRelation(MDrelation.fromInt(cursor.getInt(2)));
            model.setLunarDate(new LunarDate(cursor.getInt(3), cursor.getInt(4)));
            return model;
        }
        return null;
    }

    public List<MemorialDay> getMemorialDays(int lunarMonth, int lunarDay) {

        List<MemorialDay> result = new ArrayList<MemorialDay>();
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("memorialDay", null, "month=? AND day=?", new String[]{lunarMonth + "", lunarDay + ""}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            MemorialDay model = new MemorialDay();
            model.setId(UUID.fromString(cursor.getString(0)));
            model.setType(MDtype.fromInt(cursor.getInt(1)));
            model.setRelation(MDrelation.fromInt(cursor.getInt(2)));
            model.setLunarDate(new LunarDate(cursor.getInt(3), cursor.getInt(4)));
            result.add(model);
        }
        return result;
    }

    public List<MemorialDay> getMemorialDays() {

        List<MemorialDay> result = new ArrayList<MemorialDay>();
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("memorialDay", null, null, null, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            MemorialDay model = new MemorialDay();
            model.setId(UUID.fromString(cursor.getString(0)));
            model.setType(MDtype.fromInt(cursor.getInt(1)));
            model.setRelation(MDrelation.fromInt(cursor.getInt(2)));
            model.setLunarDate(new LunarDate(cursor.getInt(3), cursor.getInt(4)));
            result.add(model);
        }
        return result;
    }

    public void deleteMemorialDay(UUID id) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("memorialDay", "id=?", new String[]{id.toString()});
        //关闭SQLiteDatabase对象
        db.close();
    }

    public Setting getSetting(String key) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, "key=?", new String[]{key}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(key, cursor.getString(1));
            return setting;
        }
        return null;
    }

    public Setting getSetting(String key, Object defaultValue){
        Setting setting = getSetting(key);
        if (setting == null) {
            this.addSetting(key, defaultValue);
            setting = new Setting(key,defaultValue.toString());
            return setting;
        }
        return setting;
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     *
     * @param key
     * @param value
     */
    public void editSetting(String key, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("value", value.toString());
        if (db.update("setting", values, "key=?", new String[]{key}) == 0) {
            this.addSetting(key, value.toString());
        }
        db.close();
    }

    public void deleteSetting(String key) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "key=?", new String[]{key});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void addSetting(String key, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value.toString());
        //调用方法插入数据
        db.insert("setting", "key", values);
        //关闭SQLiteDatabase对象
        db.close();
    }


}
