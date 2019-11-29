package com.example.sevicetesting;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private MediaPlayer mPlayer;
    private int nextToPlay = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was started.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (nextToPlay == 1) {
            mPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
            nextToPlay = 2;
        }

        else {
            mPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_NOTIFICATION_URI);
            nextToPlay = 1;
        }

        mPlayer.setLooping(false);
        // It will start the player
        mPlayer.start();
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stopping the player when service is destroyed
        mPlayer.stop();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }
}
