package com.example.sevicetesting;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class TimerService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    Random r;
    int NotID =1;
    NotificationManager nm;

    private Messenger messenger;
    private Long times;
    private static final String TAG = "TimerService";

    public TimerService() {
        super("number5");  //or whatever name you want to give it.
        r = new Random();
        //showToast("b Intent Service started");
    }
    public TimerService(String name) {
        super(name);  //or whatever name you want to give it.
        r = new Random();
        //showToast("Intent Service started");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        messenger = null;
        Bundle extras = intent.getExtras();

        if (extras != null) {
            times = extras.getLong("times",0);
            messenger = (Messenger) extras.get("MESSENGER");
        }

        while(times > 0) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                }
            }
            if (messenger != null) {
                Message msg = Message.obtain();
                msg.obj = times - 1000;
                try {
                    messenger.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }
        }
    }

    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ........................");

        Bundle extras = intent.getExtras();

        if (extras != null) {
            times = extras.getLong("times",0);
            messenger = (Messenger) extras.get("MESSENGER");
        }

        while(times > 0) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                }
            }
            if (messenger != null) {
                Message msg = Message.obtain();
                msg.obj = times - 1000;
                try {
                    messenger.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");
        Intent notificationIntent = new Intent(this, TimerActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        return START_NOT_STICKY;
    }

     */

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "TimerActivity",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: intent in on destory" );
        Notification.Builder builder = new Notification.Builder(getBaseContext())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("Your Ticker") // use something from something from R.string
                .setContentTitle("Your content title") // use something from something from
                .setContentText("Your content text") // use something from something from
                .setProgress(0, 0, true); // display indeterminate progress

        startForeground(1, builder.build());

    }

    public void makenoti(String message) {

        Notification noti = new NotificationCompat.Builder(getApplicationContext(), "Timer Activity")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())  //When the event occurred, now, since noti are stored by time.
                .setContentTitle("Service")   //Title message top row.
                .setContentText(message)  //message when looking at the notification, second row
                .setAutoCancel(true)   //allow auto cancel when pressed.
                .build();  //finally build and return a Notification.

        //Show the notification
        nm.notify(NotID, noti);
        NotID++;
    }

}