package com.example.sevicetesting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        Long times = 0l;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            times = bundle.getLong("time");
        }
        Intent intent1 = new Intent(context, TimerService1.class);
        intent1.putExtra("time", times);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }
    }
}
