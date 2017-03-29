package com.wang17.religiouscalendar.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wang17.religiouscalendar.emnu.MDrelation;
import com.wang17.religiouscalendar.emnu.MDtype;
import com.wang17.religiouscalendar.helper._Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;
    private Context context;

    public DataContext(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    //region RunLog
    public List<RunLog> getRunLogs() {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, null, null, null, null, null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
        return result;
    }

    public void addRunLog(RunLog runLog) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", runLog.getId().toString());
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }

    public void updateRunLog(RunLog runLog) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());

            db.update("runLog", values, "id=?", new String[]{runLog.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }

    public void deleteRunLog() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", null, null);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }
    //endregion


    //region SexualDay

    /**
     * 增加一条SexualDay
     *
     * @param sexualDay 记录对象
     */
    public void addSexualDay(SexualDay sexualDay) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", sexualDay.getId().toString());
            values.put("dateTime", sexualDay.getDateTime().getTimeInMillis());
            values.put("item", sexualDay.getItem());
            values.put("summary", sexualDay.getSummary());

            //调用方法插入数据
            db.insert("sexualDay", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public SexualDay getLastSexualDay() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("sexualDay", null, null, null, null, null, "DateTime  DESC");
            //判断游标是否为空

            if (cursor.moveToNext()) {
                SexualDay model = new SexualDay(UUID.fromString(cursor.getString(0)));
                model.setDateTime(new DateTime(cursor.getLong(1)));
                model.setItem(cursor.getString(2));
                model.setSummary(cursor.getString(3));
                return model;
            }
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
        return null;
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public List<SexualDay> getSexualDays(boolean isTimeDesc) {

        List<SexualDay> result = new ArrayList<SexualDay>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("sexualDay", null, null, null, null, null, isTimeDesc ? "DateTime DESC" : null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                SexualDay model = new SexualDay(UUID.fromString(cursor.getString(0)));
                model.setDateTime(new DateTime(cursor.getLong(1)));
                model.setItem(cursor.getString(2));
                model.setSummary(cursor.getString(3));
                result.add(model);
            }
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
        return result;
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public void updateSexualDay(SexualDay sexualDay) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("dateTime", sexualDay.getDateTime().getTimeInMillis());
            values.put("item", sexualDay.getItem());
            values.put("summary", sexualDay.getSummary());

            db.update("sexualDay", values, "id=?", new String[]{sexualDay.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }

    /**
     * 删除指定的record
     *
     * @param id
     */
    public void deleteSexualDay(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("sexualDay", "id=?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Helper.printException(context, e);
        }
    }
    //endregion


    //region MemorialDay
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
    //endregion

    //region Setting
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
    //endregion


}
