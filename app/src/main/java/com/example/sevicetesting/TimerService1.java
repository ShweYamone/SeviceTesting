package com.example.sevicetesting;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;


public class TimerService1 extends Service {
    // Constants
    private static final int ID_SERVICE = 101;
    private static final String TAG="TimerService1";

    private CountDownTimer countDownTimer;
    static long myMillisUntilFinishedFromService;
    static boolean isServiceRunning = false;
    boolean timeUp = false;

     //100seconds

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the Foreground Service
        Intent notifyIntent = new Intent(this, TimerActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        notificationBuilder.setContentIntent(notifyPendingIntent);

        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Ur Timer is running...")
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();


        startForeground(ID_SERVICE, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand:......................");
        isServiceRunning = true;
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            myMillisUntilFinishedFromService = bundle.getLong("time");
            Toast.makeText(this, (myMillisUntilFinishedFromService / 1000)+"seconds.....", Toast.LENGTH_SHORT).show();
            countDownTimer = new CountDownTimer(myMillisUntilFinishedFromService, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    myMillisUntilFinishedFromService = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    timeUp = true;
                    stopSelf();
                    stopForeground(true);
                }
            };
            countDownTimer.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isServiceRunning = true;
        if (!timeUp) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("time", myMillisUntilFinishedFromService);
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
        else {
            stopForeground(true);
        }
    }

    @Override
    public boolean stopService(Intent name) {
        isServiceRunning = false;
        stopSelf();
        return super.stopService(name);
    }
}
