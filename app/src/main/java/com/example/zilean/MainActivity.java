package com.example.zilean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class MainActivity extends AppCompatActivity {
    //    private final boolean isTest = false;
    private boolean isTest = false;

    @ViewInject(R.id.ibtn_setting)
    private ImageButton ibtn_setting;

    @ViewInject(R.id.cb_context1)
    private CheckBox cb_context1;

    @ViewInject(R.id.cb_context2)
    private CheckBox cb_context2;

    @ViewInject(R.id.cb_context3)
    private CheckBox cb_context3;

    @ViewInject(R.id.tv_time)
    private TextView tv_time;

    @ViewInject(R.id.tv_state)
    private TextView tv_state;

    @ViewInject(R.id.ibtn_go)
    private ImageButton ibtn_go;

    private MyTimeHandler myTimeHandler;
    private MyGoHandler myGoHandler;
    private int[] imageId;
    private String[] stateType;
    private SharedPreferences sharedPreferences;
    private Vibrator vibrator;
    private int work;
    private int shortRest;
    private int longRest;
    private boolean autoWork;
    private boolean autoRest;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        init();

    }

    private void init() {
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        work = sharedPreferences.getInt("work", 25);
        shortRest = sharedPreferences.getInt("shortRest", 5);
        longRest = sharedPreferences.getInt("longRest", 15);
        autoWork = sharedPreferences.getBoolean("autoWork", false);
        autoRest = sharedPreferences.getBoolean("autoRest", false);
        myTimeHandler = new MyTimeHandler();
        myTimeHandler.setTomatoThread(new TomatoThread(work, 0));
        myGoHandler = new MyGoHandler();
        imageId = new int[]{R.drawable.go, R.drawable.stop};
        stateType = new String[]{"工作", "短休息", "长休息"};
    }

    public void startRemind(long[] vibratorTime) {
        //震动提醒
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
            switch (vibratorTime.length) {
                case 0:
                    break;
                case 1:
                    vibrator.vibrate(vibratorTime[0]);
                    break;
                default:
                    vibrator.vibrate(vibratorTime, -1);
                    break;
            }
        }
        //铃声提醒
    }

    @Event(value = R.id.cb_context3, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        isTest = b;
    }

    @Event(R.id.ibtn_setting)
    private void onSetting(View view) {
        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
        startActivityForResult(intent, 1);
    }

    @Event(R.id.ibtn_go)
    private void onGo(View view) {
        myGoHandler.sendEmptyMessage(0);
    }

    @Event(value = R.id.ibtn_go, type = View.OnLongClickListener.class)
    private boolean onGoLong(View view) {
        myGoHandler.sendEmptyMessage(1);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1 && requestCode == 1) {
            work = data.getIntExtra("work", work);
            shortRest = data.getIntExtra("shortRest", shortRest);
            longRest = data.getIntExtra("longRest", longRest);
            autoWork = sharedPreferences.getBoolean("autoWork", autoWork);
            autoRest = sharedPreferences.getBoolean("autoRest", autoRest);
        }
    }

    public class MyGoHandler extends Handler {
        boolean isClick = false;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://点击go
                    if (!isClick) {
                        ibtn_go.setImageResource(imageId[1]);
                        isClick = true;
                        myTimeHandler.startThread();
                    }
                    break;
                case 1://点击stop
                    if (isClick) {
                        ibtn_go.setImageResource(imageId[0]);
                        isClick = false;
                        myTimeHandler.stopThread();
                    }
                    break;
                case 2://时间到
                    if (isClick) {
                        myTimeHandler.nextThread();
                    }
                    break;
                case 3://
                    if (isClick) {
                        ibtn_go.setImageResource(imageId[0]);
                        isClick = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setViewText(final int min, final int sec) {
        myTimeHandler.post(new Runnable() {
            @Override
            public void run() {
                tv_time.setText(String.format("%02d", min) + ":" + String.format("%02d", sec));
            }
        });
    }

    public class MyTimeHandler extends Handler {
        int tomato = 0;
        int rest = 0;
        TomatoThread tomatoThread = null;
        Thread thread = null;

        public MyTimeHandler(TomatoThread tomatoThread) {
            this.tomatoThread = tomatoThread;
        }

        public MyTimeHandler() {

        }

        public void setTomatoThread(TomatoThread tomatoThread) {
            this.tomatoThread = tomatoThread;
        }

        public void startThread() {
            thread = new Thread(tomatoThread);
            thread.start();
        }

        public void nextThread() {
            sendEmptyMessage(0);
        }

        public void stopThread() {
            tomatoThread.setBreak(true);
            sendEmptyMessage(2);
        }


        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://当前计时完毕
                    if (tomato == rest) {
                        tomato++;
                        if (tomato % 4 == 0) {
                            tomatoThread = new TomatoThread(longRest, 0);
                            tv_state.setText(stateType[2]);
                        } else {
                            tomatoThread = new TomatoThread(shortRest, 0);
                            tv_state.setText(stateType[1]);
                        }
                        startRemind(new long[]{0,1000,500,2000});
                        if (autoRest) startThread();
                        else myGoHandler.sendEmptyMessage(3);
                    } else {
                        rest++;
                        tomatoThread = new TomatoThread(work, 0);
                        tv_state.setText(stateType[0]);
                        startRemind(new long[]{1000});
                        if (autoWork) startThread();
                        else myGoHandler.sendEmptyMessage(3);
                    }
                    break;
                case 1://计时中
                    setViewText(msg.arg1, msg.arg2);
                    break;
                case 2://当前计时取消
                    int time = tomatoThread.getMin();
                    tomatoThread = new TomatoThread(time, 0);
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    }

    public class TomatoThread implements Runnable {
        int min = 25;
        int sec = 0;
        int min_t = min;
        long time1 = 0;
        long time2 = 0;
        boolean isBreak = false;
        Message message = Message.obtain();

        public TomatoThread(int min, int sec) {
            if (isTest) {
                this.sec = min;
                this.min = sec;
            } else {
                this.min = min;
                this.sec = sec;
            }

            this.min_t = min;

            message.what = 1;
            message.arg1 = this.min;
            message.arg2 = this.sec;
            myTimeHandler.handleMessage(message);
        }

        public int getMin() {
            return min_t;
        }

        public void setBreak(boolean aBreak) {
            isBreak = aBreak;
        }

        @Override
        public void run() {
            while (min > 0 || sec > 0) {
                if (isBreak) break;
                time1 = System.currentTimeMillis();
                if (sec == 0) {
                    min--;
                    sec = 59;
                } else sec--;
                message.what = 1;
                message.arg1 = min;
                message.arg2 = sec;
                time2 = System.currentTimeMillis();
                try {
                    Thread.sleep(1000 - (time2 - time1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isBreak) break;
                myTimeHandler.handleMessage(message);
            }
            if (!isBreak) myGoHandler.sendEmptyMessage(2);
        }
    }


}
