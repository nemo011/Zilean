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

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//@ContentView(R.layout.activity_dialog)
public class DialogActivity extends AppCompatActivity {
    private final boolean ISENABLE_ET = false;
    private final int RESULT_CAMERA = 1;
    private final int RESULT_PHOTO = 2;
    private final int RESULT_CROP = 3;
    private final String FILENAME = "background.png";
    private final String FILENAME_TEMP = "background_temp.png";
    private final String FILE_DIR = "Zilean";
    private final String PACKET_NAME = "com.example.zilean";

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
        String path1 = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR;
        String path2 = "/data" + Environment.getDataDirectory().getAbsolutePath() + File.separator + PACKET_NAME + "/files/";
        file = new File(isSDExist ? path1 : path2, FILENAME);
        tempFile = new File(isSDExist ? path1 : path2, FILENAME_TEMP);

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
                .putBoolean("isRing", isRing).commit();
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
        savePhoto(tempFile, file);
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
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为RESULT_PHOTO
        startActivityForResult(intent, RESULT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_PHOTO) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
//                Log.e("路径1", uri.toString());
                if (tempFile.exists()) tempFile.delete();
                crop(uri);
            }
        } else if (requestCode == RESULT_CROP) {
            // 从剪切图片返回的数据
            if (data != null) {
                isBackground = true;
                if (!isSDExist) {
                    bitmap = data.getParcelableExtra("data");
                    try {
                        saveBitmap(bitmap, tempFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// 发送裁剪信号
        // 裁剪框的比例
        intent.putExtra("aspectX", 9);
        intent.putExtra("aspectY", 16);
        intent.putExtra("scale", true);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", screenWidth);
        intent.putExtra("outputY", screenHeight);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        if (isSDExist) {
            //以Uri格式可以保存大分辨率的图，但必须要SD卡支持
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file:///sdcard/"+FILENAME_STORAGE));
        } else {
            //Bitmap格式传的是小分辨率的图，只适合小图裁剪
            intent.putExtra("return-data", true);// 是否将数据保留在Bitmap中返回
        }
        // 开启一个带有返回值的Activity，请求码为RESULT_CROP
        startActivityForResult(intent, RESULT_CROP);
    }

    private void saveBitmap(Bitmap bitmap, File file) throws IOException {
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) parentFile.mkdirs();
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void savePhoto(File temp, File file) {
        if (temp.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) parentFile.mkdirs();
            if (file.exists()) file.delete();
            byte[] bytes = new byte[1024];
            int i = -1;
            try {
                FileInputStream fis = new FileInputStream(temp);
                FileOutputStream fos = new FileOutputStream(file);
                while ((i = fis.read(bytes)) != -1) fos.write(bytes, 0, i);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            temp.delete();
        }
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
