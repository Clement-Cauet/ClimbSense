package com.climbsense.application;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Chronometer;

import androidx.annotation.Nullable;

import com.climbsense.application.function.InstanceFunction;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChronometerService extends Service {

    private InstanceFunction instanceFunction;

    private Chronometer chronometer;

    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        instanceFunction = InstanceFunction.getInstance();
        long start = SystemClock.elapsedRealtime();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = SystemClock.elapsedRealtime() - start;
                Map<String, Object> map = new HashMap<>();
                map.put("time", elapsedMillis);
                map.put("acceleration", new Random().nextInt(15 + 1));
                map.put("height", new Random().nextInt(200 + 1) + 100);
                map.put("bpm", new Random().nextInt(120 + 1) + 60);
                map.put("temperature", new Random().nextInt(20 + 1) + 20);
                map.put("humidity", new Random().nextInt(100));
                instanceFunction.setClimbInfo(map);
                EventBus.getDefault().post(new ChronometerEvent(map));
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
