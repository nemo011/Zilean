package com.example.zilean.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.zilean.Bean.DailyTime;
import com.example.zilean.Bean.UseRecord;
import com.example.zilean.TheUtils.Constant;

/**
 * Created by 王跃_ on 2016/11/25.
 */

public class MyDatabase {
    Context context;
    MyOpenHelper myOpenHelper;
    SQLiteDatabase db;
    String name;

    public MyDatabase(Context con, String name) {
        this.context = con;
        this.name = name;
        myOpenHelper = new MyOpenHelper(context, name);
        db = myOpenHelper.getWritableDatabase();
    }

    public MyDatabase(Context con) {
        this(con, "zilean_data");
    }

    public void close() {
        if (db.isOpen()) db.close();
    }

    //方法
    public void doSync(int id, String table) {
        db.execSQL("update " + table + " set sync = '1' where id = " + id);
    }

    public void unSync(int id, String table) {
        db.execSQL("update " + table + " set sync = '0' where id = " + id);
    }

    //DAILY_TIME
    public int insertDailyTime(String user, int time, String date) {
        String sql = "insert into " + Constant.DAILY_TIME + "(user,time,date) values('" + user + "','" + time + "','" + date + "')";
        db.execSQL(sql);
        return getDailyId(user, date);
    }

    public int insertDailyTime(DailyTime dailyTime) {
        return insertDailyTime(dailyTime.getUser(), dailyTime.getTime(), dailyTime.getDate());
    }

    public int getDailyId(String user, String date) {
        int id = 0;
        String sql = "select id from " + Constant.DAILY_TIME + " where user = '" + user + "' and date = '" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }

    public int[] getSumTime(String user, String date1, String date2) {
        int[] time = new int[]{0, 0};
        String sql = "select time,date from " + Constant.DAILY_TIME + " where user = '" + user + "' and date < '" + date1 + "' order by id desc";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int t = cursor.getInt(cursor.getColumnIndex("time"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                time[0] += t;
                if ((date.compareTo(date2)) > 0)
                    time[1] = time[0];
                cursor.moveToNext();
            }
        }
        return time;
    }

    public DailyTime getDailyTime(String user, String date) {
        DailyTime dailyTime = new DailyTime();
        String sql = "select * from " + Constant.DAILY_TIME + " where user = '" + user + "' and date = '" + date + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            dailyTime = new DailyTime(id, user, time, date);
        }
        return dailyTime;
    }

    public void updateDayTime(int id, int min) {
        String sql = "update " + Constant.DAILY_TIME + " set time = time + '" + min + "' where id = " + id;
        db.execSQL(sql);
        unSync(id, Constant.DAILY_TIME);
    }


    //USE_RECORD
    public int insertUseRecord(String user, int time, String date, String begin) {
        String sql = "insert into " + Constant.USE_RECORD + "(user,time,date,begin) values('" + user + "','" + time + "','" + date + "','" + begin + "')";
        db.execSQL(sql);
        return getUseId(user, date, begin);
    }

    public int insertUseRecord(UseRecord useRecord) {
        return insertUseRecord(useRecord.getUser(), useRecord.getTime(), useRecord.getDate(), useRecord.getBegin());
    }

    public int getUseId(String user, String date, String begin) {
        int id = 0;
        String sql = "select id from " + Constant.USE_RECORD + " where user = '" + user + "' and date = '" + date + "' and begin = '" + begin + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }

    public UseRecord getLastUseRecord(String user, String date) {
        UseRecord useRecord = new UseRecord();
        String sql = "select * from " + Constant.USE_RECORD + " where user = '" + user + "' and date = '" + date + "' order by id desc";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int time = cursor.getInt(cursor.getColumnIndex("time"));
            String begin = cursor.getString(cursor.getColumnIndex("begin"));
            String end = cursor.getString(cursor.getColumnIndex("end"));
            int state = cursor.getInt(cursor.getColumnIndex("state"));
            String cancelReason = cursor.getString(cursor.getColumnIndex("cancel_reason"));
            useRecord = new UseRecord(id, user, time, date, begin, end, state, cancelReason);
        }
        return useRecord;
    }

    public void updateUseRecordFinish(int id, String date, String end) {
        String sql = "update " + Constant.USE_RECORD + " set date = '" + date + "',end = '" + end + "',state = '1' where id = " + id;
        db.execSQL(sql);
//        unSync(id, Constant.USE_RECORD);
    }

    public void updateUseRecordCancle(int id, String cancelReason) {
        String sql = "update " + Constant.USE_RECORD + " set state = '2',cancel_reason = '" + cancelReason + "' where id = " + id;
        db.execSQL(sql);
//        unSync(id, Constant.USE_RECORD);
    }
    //TASK_RECORD
}
