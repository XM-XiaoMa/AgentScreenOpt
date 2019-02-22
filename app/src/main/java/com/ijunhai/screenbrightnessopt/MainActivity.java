package com.ijunhai.screenbrightnessopt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ijunhai.util.ScreenBrightnessOpt;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {

    private EditText brightness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
        ScreenBrightnessOpt.getInstance().onCreate(MainActivity.this);
        ScreenBrightnessOpt.getInstance().setActivityUploadCallback(new ScreenBrightnessOpt.IActivityUpload() {
            @Override
            public Activity getActivity() {
                return MainActivity.this;
            }
        });
    }

    private void initData() {
        int screenBrightness = ScreenBrightnessOpt.getInstance().getScreenBrightness(MainActivity.this);
        brightness.setHint(String.valueOf(screenBrightness));
    }

    private void initView() {
        brightness = (EditText) findViewById(R.id.brightness);
    }

    private void initListener() {
        findViewById(R.id.done).setOnClickListener(this);
        findViewById(R.id.timer).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
        findViewById(R.id.curr_brightness).setOnClickListener(this);
        findViewById(R.id.second_activity).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                String trim = brightness.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) trim = "100";
                int brightness = Integer.valueOf(trim);
                ScreenBrightnessOpt.getInstance().setScreenManualMode(MainActivity.this);
                ScreenBrightnessOpt.getInstance().setWindowBrightness(MainActivity.this, brightness);
                break;
            case R.id.timer:
                //ScreenBrightnessOpt.getInstance().optWindowBrightnessAuto(MainActivity.this);
                break;
            case R.id.reset:
                ScreenBrightnessOpt.getInstance().setScreenAutoMode(MainActivity.this);
                break;
            case R.id.second_activity:
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case R.id.curr_brightness:
                Toast.makeText(MainActivity.this, ScreenBrightnessOpt.getInstance().getScreenBrightness(MainActivity.this) + "", Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ScreenBrightnessOpt.getInstance().dispatchTouchEvent(ev, MainActivity.this);
        return super.dispatchTouchEvent(ev);
    }


}
