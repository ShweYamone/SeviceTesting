package com.example.sevicetesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {


    TextView tvTime;
    Button btnStart;
    Button btnStop;
    Button btnNext;

    private boolean isRunning = false;
    private CountDownTimer countDownTimer;
    private long myMillisUntilFinished = 20000; //100seconds
    private static final String TAG = "TimerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        tvTime = findViewById(R.id.tvTime);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnNext = findViewById(R.id.btnNext);


        if (TimerService1.isServiceRunning == true) {
            stopService(new Intent(this, TimerService1.class));

            Log.i(TAG, "onCreate: ....." + TimerService1.myMillisUntilFinishedFromService);
            myMillisUntilFinished = TimerService1.myMillisUntilFinishedFromService;
            processCountDownTimer(myMillisUntilFinished);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = true;
                processCountDownTimer(myMillisUntilFinished);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer != null)
                    countDownTimer.cancel();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });

    }

    private void processCountDownTimer(long millis) {
        Log.i(TAG, "processCountDownTimer: " + millis);

        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                myMillisUntilFinished = millisUntilFinished;
                tvTime.setText((millisUntilFinished / 1000) + "s left...");
            }

            @Override
            public void onFinish() {
                tvTime.setText("Done!!!");
            }
        };
        countDownTimer.start();
    }

    private void callTimerService(String serviceAction) {
        Intent intent = new Intent(this, TimerService1.class);
        intent.setAction(serviceAction);
        intent.putExtra("time", myMillisUntilFinished);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isRunning)
            processCountDownTimer(myMillisUntilFinished);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRunning)
            processCountDownTimer(myMillisUntilFinished);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning)
            processCountDownTimer(myMillisUntilFinished);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callTimerService("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        callTimerService("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        callTimerService("Foreground");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  stopService(new Intent(this, TimerService1.class));
        callTimerService("");
    }
}
