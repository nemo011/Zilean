package com.example.zilean.Database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zilean.TheUtils.Constant;

/**
 * Created by 王跃_ on 2016/11/25.
 */

public class MyOpenHelper extends SQLiteOpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyOpenHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 每日总时长的数据表
         * id:记录的id
         * user:该记录对应的用户名
         * time:该记录的时长（n分钟）
         * date:该记录的日期（2016-07-11）
         * sync:该记录是否与服务器同步
         */
        db.execSQL("create table if not exists "+ Constant.DAILY_TIME+"(" +
                "id integer PRIMARY KEY autoincrement," +
                "user text," +
                "time integer," +
                "date text," +
                "sync integer default '0')");

        /**
         * 单个时钟的数据表
         * id:记录的id
         * user:该记录对应的用户名
         * time:该次时钟的时长（n分钟）
         * date:该次时钟的日期（2016-07-11）
         * begin:该时钟开始的时间（23:11）
         * end:该时钟结束的时间（23:41）
         * state:该时钟的状态（运行中/完成/取消）
         * cancel_reason:该时钟取消的理由
         * sync:该记录是否与服务器同步
         */
        db.execSQL("create table if not exists "+ Constant.USE_RECORD+"(" +
                "id integer PRIMARY KEY autoincrement," +
                "user text," +
                "time integer," +
                "date text," +
                "begin text," +
                "end text default ''," +
                "state integer default '0'," +
                "cancel_reason text default ''," +
                "sync integer default '0')");

        /**
         * 任务的数据表
         * id:记录的id
         * user:该任务对应的用户名
         * time:该任务完成的时间（23:11）
         * date:该任务完成的日期（2016-07-11）
         * content:该任务的内容
         * mark:任务标记
         * state:该任务的状态（完成/未完成）
         * sync:该记录是否与服务器同步
         */
        db.execSQL("create table if not exists "+ Constant.TASK_RECORD+"(" +
                "id integer PRIMARY KEY autoincrement," +
                "user text," +
                "time text default ''," +
                "date text default ''," +
                "content text," +
                "mark integer default '0'," +
                "state integer default '0'," +
                "sync integer default '0')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j) {

    }
}
