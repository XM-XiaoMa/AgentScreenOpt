package com.ijunhai.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class ScreenBrightnessOpt {

    private static ScreenBrightnessOpt instance = null;

    private ScreenBrightnessOpt() {
    }

    public static synchronized ScreenBrightnessOpt getInstance() {
        if (instance == null) {
            synchronized (ScreenBrightnessOpt.class) {
                instance = new ScreenBrightnessOpt();
            }
        }
        return instance;
    }

    /**
     * 屏幕亮度自动调节模式
     *
     * @param activity
     */
    public void setScreenAutoMode(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 屏幕亮度手动调节模式
     *
     * @param activity
     */
    public void setScreenManualMode(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            int mode = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取系统屏幕亮度值
     *
     * @return
     */
    public int getScreenBrightness(Activity activity) {
        ContentResolver contentResolver = activity.getContentResolver();
        int defVal = 125;
        return Settings.System.getInt(contentResolver,
                Settings.System.SCREEN_BRIGHTNESS, defVal);
    }

    /**
     * 设置当前窗口亮度
     * <p>
     * 屏幕最大亮度为255。
     * 屏幕最低亮度为0。
     * 屏幕亮度值范围必须位于：0～255。
     *
     * @param activity
     * @param brightness
     */
    public void setWindowBrightness(Activity activity, float brightness) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);
    }


    /**
     * 屏幕监听
     */
    public void dispatchTouchEvent(MotionEvent ev, Activity activity) {
        if (task == null) return;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(activity);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(activity);
                break;
        }
    }

    /*
     * 下方是Activity对象获取操作
     */
    private IActivityUpload activityUpload;


    public interface IActivityUpload {
        Activity getActivity();
    }

    public void setActivityUploadCallback(IActivityUpload iActivityUpload) {
        activityUpload = iActivityUpload;
    }

    /*
     * 下方时生命周期
     */

    public void onCreate(Activity activity) {
        startTimer();
    }

    /*
     * 下方是屏幕操作关键函数
     */
    private void resetScreenBrightness(final Activity activity) {
        Log.d("MainActivity", "resetScreenBrightness");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("MainActivity", "重置屏幕亮度");
                int systemBrightness = ScreenBrightnessOpt.getInstance().getScreenBrightness(activity);
                ScreenBrightnessOpt.getInstance().setScreenManualMode(activity);
                ScreenBrightnessOpt.getInstance().setWindowBrightness(activity, (float) systemBrightness);
            }
        });

    }

    private void reduceScreenBrightness(final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("MainActivity", "降低屏幕亮度");
                int systemBrightness = ScreenBrightnessOpt.getInstance().getScreenBrightness(activity);
                double brightness = systemBrightness * 0.2;
                ScreenBrightnessOpt.getInstance().setScreenManualMode(activity);
                ScreenBrightnessOpt.getInstance().setWindowBrightness(activity, (float) brightness);
            }
        });

    }

    /*
     * 下方是定时操作
     */

    private MyTask task;
    private Timer timer;
    private long currentTime;
    private static final int TIME_LIMIT = 10;

    class MyTask extends TimerTask {
        @Override
        public void run() {
            currentTime++;
            Log.d("MainActivity", "currentTime : " + currentTime);
            if (currentTime > TIME_LIMIT && currentTime < TIME_LIMIT + 2 && activityUpload != null) {
                reduceScreenBrightness(activityUpload.getActivity());
            }
            if (currentTime > TIME_LIMIT + 3) {
                stopTimer();
            }
        }

    }

    private void handleActionUp(Activity activity) {
        startTimer();
    }

    private void handleActionDown(Activity activity) {
        // 重置屏幕亮度
        Log.d("MainActivity", "handleActionDown");
        if (currentTime > TIME_LIMIT) {
            resetScreenBrightness(activity);
        }
        stopTimer();
    }

    private void startTimer() {
        currentTime = 0;
        initTimer();
        try {
            timer.schedule(task, 1000, 1000);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            initTimer();
            timer.schedule(task, 1000, 1000);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void initTimer() {
        // 初始化计时器
        task = new MyTask();
        timer = new Timer();
    }
}
