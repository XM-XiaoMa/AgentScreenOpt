package com.ijunhai.util;

import java.util.Timer;

public class ScreenTimer {

    private static ScreenTimer instance = null;
    private static final int SCREEN_OFF = 30 * 1000;

    private ScreenTimer() {
    }

    public static synchronized ScreenTimer getInstance() {
        if (instance == null) {
            synchronized (ScreenTimer.class) {
                instance = new ScreenTimer();
            }
        }
        return instance;
    }

    public void setTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }

}
