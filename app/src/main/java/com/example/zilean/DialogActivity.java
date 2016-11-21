package com.example.zilean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.zilean.TheUtils.Constant;
import com.example.zilean.TheUtils.Utils;
import com.soundcloud.android.crop.Crop;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;

//@ContentView(R.layout.activity_dialog)
public class DialogActivity extends AppCompatActivity {
    private final boolean isLog = false;
    private final boolean ISENABLE_ET = false;

    @ViewInject(R.id.et_work)
    private EditText et_work;

    @ViewInject(R.id.et_shortRest)
    private EditText et_shortRest;

    @ViewInject(R.id.et_longRest)
    private EditText et_longRest;

    @ViewInject(R.id.sb_work)
    private SeekBar sb_work;

    @ViewInject(R.id.sb_shortRest)
    private SeekBar sb_shortRest;

    @ViewInject(R.id.sb_longRest)
    private SeekBar sb_longRest;

    @ViewInject(R.id.btn_ok)
    private Button btn_ok;

    @ViewInject(R.id.btn_cancel)
    private Button btn_cancel;

    @ViewInject(R.id.cb_work)
    private CheckBox cb_work;

    @ViewInject(R.id.cb_rest)
    private CheckBox cb_rest;

    @ViewInject(R.id.tb_shark)
    private ToggleButton tb_shark;

    @ViewInject(R.id.tb_ring)
    private ToggleButton tb_ring;

    @ViewInject(R.id.tv_background)
    private TextView tv_background;

    private SharedPreferences sharedPreferences;
    private int work;
    private int shortRest;
    private int longRest;
    private boolean autoWork;
    private boolean autoRest;
    private boolean isShark;
    private boolean isRing;
    private boolean isBackground;
    private Bitmap bitmap;
    private int screenWidth;
    private int screenHeight;
    private int screenGcd;
    private File file;
    private File tempFile;
    private boolean isSDExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        x.view().inject(this);
        init();
    }

    private void init() {
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        work = sharedPreferences.getInt("work", 25);
        shortRest = sharedPreferences.getInt("shortRest", 5);
        longRest = sharedPreferences.getInt("longRest", 15);
        autoWork = sharedPreferences.getBoolean("autoWork", false);
        autoRest = sharedPreferences.getBoolean("autoRest", false);
        isShark = sharedPreferences.getBoolean("isShark", true);
        isRing = sharedPreferences.getBoolean("isRing", false);
        isBackground = false;
        bitmap = null;
        isSDExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        isSDExist = false;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        screenGcd = Utils.gcd(screenHeight, screenWidth);
        Utils.showLog("比例", screenWidth / screenGcd + ":" + screenHeight / screenGcd, isLog);
//        String path1 = Environment.getExternalStorageDirectory() + File.separator + Constant.FILE_DIR;
//        String path2 = "/data/data/" + getPackageName() + "/files";
        String path1 = getExternalFilesDir(null).getAbsolutePath();
        String path2 = getFilesDir().getAbsolutePath();
        Utils.showLog("路径1", path1, isLog);
        Utils.showLog("路径2", path2, isLog);
//        isSDExist = false;
        file = new File(isSDExist ? path1 : path2, Constant.FILENAME);
        tempFile = new File(isSDExist ? path1 : path2, Constant.FILENAME_TEMP);
        Utils.mkdirsFile(tempFile);

        et_work.setEnabled(ISENABLE_ET);
        et_shortRest.setEnabled(ISENABLE_ET);
        et_longRest.setEnabled(ISENABLE_ET);
        et_work.addTextChangedListener(new MyWacher(et_work, sb_work));
        et_shortRest.addTextChangedListener(new MyWacher(et_shortRest, sb_shortRest));
        et_longRest.addTextChangedListener(new MyWacher(et_longRest, sb_longRest));
        sb_work.setProgress(work);
        sb_shortRest.setProgress(shortRest);
        sb_longRest.setProgress(longRest);
        cb_work.setChecked(autoWork);
        cb_rest.setChecked(autoRest);
        tb_shark.setChecked(isShark);
        tb_ring.setChecked(isRing);

    }


    @Event(R.id.btn_ok)
    private void onOk(View view) {
        sharedPreferences.edit().putInt("work", work)
                .putInt("shortRest", shortRest)
                .putInt("longRest", longRest)
                .putBoolean("autoWork", autoWork)
                .putBoolean("autoRest", autoRest)
                .putBoolean("isShark", isShark)
                .putBoolean("isRing", isRing).apply();
        Intent intent = new Intent();
        intent.putExtra("work", work);
        intent.putExtra("shortRest", shortRest);
        intent.putExtra("longRest", longRest);
        intent.putExtra("autoWork", autoWork);
        intent.putExtra("autoRest", autoRest);
        intent.putExtra("isShark", isShark);
        intent.putExtra("isRing", isRing);
        intent.putExtra("isBackground", isBackground);
        setResult(RESULT_OK, intent);
        Utils.file2File(tempFile, file);
        finish();
    }

    @Event(R.id.btn_cancel)
    private void onCancel(View view) {
        setResult(0);
        finish();
    }

    @Event(value = {R.id.sb_work, R.id.sb_shortRest, R.id.sb_longRest},
            type = SeekBar.OnSeekBarChangeListener.class,
            method = "onProgressChanged")
    private void onSeekChange(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.sb_work:
                work = progress;
                et_work.setText("" + progress);
                et_work.setSelection(("" + progress).length());
                break;
            case R.id.sb_shortRest:
                shortRest = progress;
                et_shortRest.setText("" + progress);
                et_shortRest.setSelection(("" + progress).length());
                break;
            case R.id.sb_longRest:
                longRest = progress;
                et_longRest.setText("" + progress);
                et_longRest.setSelection(("" + progress).length());
                break;
        }
    }

    @Event(value = {R.id.cb_work, R.id.cb_rest}, type = CompoundButton.OnCheckedChangeListener.class)
    private void checkCbChange(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_work:
                autoWork = b;
                break;
            case R.id.cb_rest:
                autoRest = b;
                break;
        }
    }

    @Event(value = {R.id.tb_shark, R.id.tb_ring}, type = CompoundButton.OnCheckedChangeListener.class)
    private void checkSwChange(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.tb_shark:
                isShark = b;
                break;
            case R.id.tb_ring:
                isRing = b;
                break;
        }
    }

    @Event(R.id.tv_background)
    private void onBackground(View view) {
        // 激活系统图库，选择一张图片
        /* new Intent(Intent.ACTION_PICK)方式 */
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        // 开启一个带有返回值的Activity，请求码为RESULT_PHOTO
//        startActivityForResult(intent, REQUEST_PHOTO);

        /* new Intent("android.intent.action.GET_CONTENT")方式 */
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            isBackground = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(tempFile);
        Crop crop = Crop.of(source, destination)
                .withAspect(screenWidth / screenGcd, screenHeight / screenGcd);
        crop.start(this);
    }
    public class MyWacher implements TextWatcher {
        private EditText et = null;
        private SeekBar sb = null;
        private CharSequence c = "";

        public MyWacher(EditText et, SeekBar sb) {
            this.et = et;
            this.sb = sb;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) s = "0" + s;
            if (!s.equals(c)) {
                int i = Integer.parseInt(s.toString());
                if (i > 60) {
                    c = "60";
                    et.setText(c);
                    et.setSelection(2);
                } else if (i < 1) {
                    c = "1";
                    et.setText(c);
                    et.setSelection(1);
                } else {
                    c = s;
                    sb.setProgress(i);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
