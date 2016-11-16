package com.example.zilean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

//@ContentView(R.layout.activity_dialog)
public class DialogActivity extends AppCompatActivity {
    private final boolean isEtEnable = false;

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

    private SharedPreferences sharedPreferences;
    private int work;
    private int shortRest;
    private int longRest;
    private boolean autoWork;
    private boolean autoRest;

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

        et_work.setEnabled(isEtEnable);
        et_shortRest.setEnabled(isEtEnable);
        et_longRest.setEnabled(isEtEnable);
        et_work.addTextChangedListener(new MyWacher(et_work, sb_work));
        et_shortRest.addTextChangedListener(new MyWacher(et_shortRest, sb_shortRest));
        et_longRest.addTextChangedListener(new MyWacher(et_longRest, sb_longRest));
        sb_work.setProgress(work);
        sb_shortRest.setProgress(shortRest);
        sb_longRest.setProgress(longRest);
        cb_work.setChecked(autoWork);
        cb_rest.setChecked(autoRest);

    }


    @Event(R.id.btn_ok)
    private void onOk(View view) {
        sharedPreferences.edit().putInt("work", work)
                .putInt("shortRest", shortRest)
                .putInt("longRest", longRest)
                .putBoolean("autoWork", autoWork)
                .putBoolean("autoRest", autoRest).commit();
        Intent intent = new Intent();
        intent.putExtra("work", work);
        intent.putExtra("shortRest", shortRest);
        intent.putExtra("longRest", longRest);
        intent.putExtra("autoWork", autoWork);
        intent.putExtra("autoRest", autoRest);
        setResult(1, intent);
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
    private void checkChange(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_work:
                autoWork = b;
                break;
            case R.id.cb_rest:
                autoRest = b;
                break;
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
