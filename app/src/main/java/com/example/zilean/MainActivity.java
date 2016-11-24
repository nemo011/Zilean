package com.example.zilean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zilean.TheUtils.Constant;
import com.example.zilean.TheUtils.Utils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //    private final boolean isTest = false;
    private final boolean isLog = true;
    private final int REQUEST_SET = 1;
    private int back_count = 0;
    private boolean isTest = false;

    @ViewInject(R.id.activity_main)
    private RelativeLayout activity_main;

    @ViewInject(R.id.iv_background)
    private ImageView iv_background;

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
    private MediaPlayer mediaPlayer;
    private int work;
    private int shortRest;
    private int longRest;
    private boolean autoWork;
    private boolean autoRest;
    private boolean isShark;
    private boolean isRing;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        if (!isTaskRoot()) {
            Utils.showLog("MainActivity","创建2次",isLog);
            finish();
            return;
        }
        init();
        setBackground();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        work = sharedPreferences.getInt("work", 25);
        shortRest = sharedPreferences.getInt("shortRest", 5);
        longRest = sharedPreferences.getInt("longRest", 15);
        autoWork = sharedPreferences.getBoolean("autoWork", false);
        autoRest = sharedPreferences.getBoolean("autoRest", false);
        isShark = sharedPreferences.getBoolean("isShark", true);
        isRing = sharedPreferences.getBoolean("isRing", false);
        myTimeHandler = new MyTimeHandler();
        myTimeHandler.setTomatoCount(new TomatoCount(work, Constant.TOMATOTYPE_WORK));
        myGoHandler = new MyGoHandler();
        imageId = new int[]{R.drawable.go, R.drawable.stop};
        stateType = new String[]{"工作", "短休息", "长休息"};
        //铃声
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer player) {
                player.seekTo(0);
            }
        });
        AssetFileDescriptor file = this.getResources().openRawResourceFd(R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(5.0f, 5.0f);
//            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }

    public void startRemind(long[] vibratorTime) {
        //震动提醒
        if (vibrator.hasVibrator() && isShark) {
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
        if (mediaPlayer != null && isRing) {
            mediaPlayer.start();
        }
    }

    @Event(value = R.id.cb_context3, type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        isTest = b;
    }

    @Event(R.id.ibtn_setting)
    private void onSetting(View view) {
        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
        startActivityForResult(intent, REQUEST_SET);
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_SET) {
            work = data.getIntExtra("work", work);
            shortRest = data.getIntExtra("shortRest", shortRest);
            longRest = data.getIntExtra("longRest", longRest);
            autoWork = data.getBooleanExtra("autoWork", autoWork);
            autoRest = data.getBooleanExtra("autoRest", autoRest);
            isShark = data.getBooleanExtra("isShark", isShark);
            isRing = data.getBooleanExtra("isRing", isRing);
            if (data.getBooleanExtra("isBackground", false)) {
                setBackground();
            }
            myTimeHandler.stopCount();
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
                        myTimeHandler.startCount();
                    }
                    break;
                case 1://点击stop
                    if (isClick) {
                        myTimeHandler.stopCount();
                    }
                    break;
                case 2://时间到
                    if (isClick) {
                        myTimeHandler.nextCount();
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

    public void setBackground() {
        //路径：data/包名/files
        String path1 = getExternalFilesDir(null).getAbsolutePath();
        String path2 = getFilesDir().getAbsolutePath();
        boolean isSDExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        isSDExist = false;
        File file = new File(isSDExist ? path1 : path2, Constant.FILENAME);
        if (file.exists()) {
            Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
            iv_background.setImageDrawable(drawable);
        }
    }

    public class MyTimeHandler extends Handler {
        int tomato = 0;
        int rest = 0;
        TomatoCount tomatoCount = null;

        public MyTimeHandler() {
        }

        public void setTomatoCount(TomatoCount tomatoCount) {
            this.tomatoCount = tomatoCount;
        }

        public void startCount() {
            if (tomatoCount != null)
                tomatoCount.start();
        }

        public void nextCount() {
            sendEmptyMessage(0);
        }

        public void stopCount() {
            if (tomatoCount != null)
                tomatoCount.cancel();
            sendEmptyMessage(2);
            myGoHandler.sendEmptyMessage(3);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://当前计时完毕
                    if (tomato == rest) {
                        tomato++;
                        if (tomato % 4 == 0) {
                            tomatoCount = new TomatoCount(longRest, Constant.TOMATOTYPE_LONG);
                            tv_state.setText(stateType[2]);
                        } else {
                            tomatoCount = new TomatoCount(shortRest, Constant.TOMATOTYPE_SHORT);
                            tv_state.setText(stateType[1]);
                        }
                        startRemind(new long[]{0, 1000, 500, 2000});
                        if (autoRest)
                            startCount();
                        else myGoHandler.sendEmptyMessage(3);
                    } else {
                        rest++;
                        tomatoCount = new TomatoCount(work, Constant.TOMATOTYPE_WORK);
                        tv_state.setText(stateType[0]);
                        startRemind(new long[]{1000});
                        if (autoWork)
                            startCount();
                        else myGoHandler.sendEmptyMessage(3);
                    }
                    break;
                case 1://计时中
                    setViewText(msg.arg1, msg.arg2);
                    break;
                case 2://当前计时取消
                    int type = tomatoCount.getType();
                    switch (type) {
                        case Constant.TOMATOTYPE_WORK:
                            tomatoCount = new TomatoCount(work, Constant.TOMATOTYPE_WORK);
                            break;
                        case Constant.TOMATOTYPE_SHORT:
                            tomatoCount = new TomatoCount(shortRest, Constant.TOMATOTYPE_SHORT);
                            break;
                        case Constant.TOMATOTYPE_LONG:
                            tomatoCount = new TomatoCount(longRest, Constant.TOMATOTYPE_LONG);
                            break;
                        default:
                            break;
                    }
                    break;
                case 3://
                    break;
                default:
                    break;
            }
        }
    }

    public class TomatoCount extends CountDownTimer {
        Message message = Message.obtain();
        int type = 0;

        public TomatoCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public TomatoCount(int min, int type) {
            this(isTest ? min * 1000L : min * 60000L, 100L);
            this.type = type;
            message.what = 1;
            if (isTest) {
                message.arg1 = 0;
                message.arg2 = min;
            } else {
                message.arg1 = min;
                message.arg2 = 0;
            }
            myTimeHandler.handleMessage(message);
        }

        public int getType() {
            return type;
        }

        @Override
        public void onTick(long l) {
            l /= 1000;
            long min = l / 60;
            long sec = l % 60;
//            message.what = 1;
            message.arg1 = (int) min;
            message.arg2 = (int) sec;
            myTimeHandler.handleMessage(message);
//            Utils.showLog("time3", min + ":" + sec, isLog);
        }

        @Override
        public void onFinish() {
            myGoHandler.sendEmptyMessage(2);
        }
    }

    @Override
    public void onBackPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (back_count == 0) {
                    myTimeHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.getToast(MainActivity.this, "再按一次退出程序").show();
                        }
                    });
                } else if (back_count == 1) {
                    myTimeHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.finish();
                        }
                    });
                }
                back_count++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                back_count = 0;
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        if (myTimeHandler != null) {
            myTimeHandler.stopCount();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
