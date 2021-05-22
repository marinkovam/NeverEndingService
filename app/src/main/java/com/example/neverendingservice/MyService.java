
package com.example.neverendingservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    protected static final int NOT_ID = 15;
    private static String TAG = "Service";

    private static Service currentService;
    private int timeCounter ;
    private static Timer timer;
    private TimerTask timerTask;
    public NetworkCheckClass networkCheck;
    public Context context;
    public int pingNumber = 0;

    private final static int INTERVAL = 600*1000;



    public MyService() {

        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        currentService = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Log.d(TAG , "Restarting service  ");
        networkCheck = new NetworkCheckClass(this);
        boolean network = networkCheck.networkCheck();

        if(network){
            Log.i("MAKE_PING","START_COMAND: Connected to the internet");
            Thread t = new Thread(){
                public void run(){
                    taskDelayLoop();
                }
            };
            t.start();
        }else{
            Log.i("START_COMMAND","NOT connected to the internet");
        }

        Log.i("MAKE_PING","START_COMMAND: The onStartCommand() is called");

        SharedPreferences prefs= getSharedPreferences("com.example.neverendingservice.ActiveServiceRunning", MODE_PRIVATE);

        if(prefs.getInt("timeCounter",0)!=0){
            timeCounter = prefs.getInt("counter",0);
        }

        if (intent == null){
            HelperLaunchServiceClass.launchService(this);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();

        return START_STICKY;
    }

    public void taskDelayLoop(){
        while(true){

            new NetworkPING(getApplicationContext()).execute();
            Log.i("MAKE_PING","doInBackground() starts");
            try{
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void startTimer() {
        Log.i(TAG , "Starting timer");

        if(timer != null){
            timer.cancel();
            timer = null;
        }

        timer = new Timer();

        initializeTimerTask();

        Log.i("SERVICE", "Scheduling...");
        timer.schedule(timerTask, 1000, 1000);
    }

    private void initializeTimerTask() {
        Log.i("SERVICE", "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (timeCounter++));
            }
        };
    }

    private void restartForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            try {
                ServiceNotification notification = new ServiceNotification();
                startForeground(NOT_ID,notification.setNotification(this, "Service notification", "This is the service's notification",R.drawable.ic_android));
                Log.i(TAG,"Restarting foreground successful");
                startTimer();
            }catch (Exception e){
                Log.e(TAG , "Error in notification " + e.getMessage());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG,"Service destroyed");

        try {
            SharedPreferences prefs= getSharedPreferences("com.example.neverendingservice.ActiveServiceRunning", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("timeCounter", timeCounter);
            editor.apply();

        } catch (NullPointerException e) {
            Log.e("SERVER", "Error " +e.getMessage());

        }

        Intent broadcastIntent = new Intent("com.example.neverendingservice");
        sendBroadcast(broadcastIntent);

        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.i(TAG , "onTaskRemoved called");

        Intent broadcastIntent = new Intent("com.example.neverendingservice");
        sendBroadcast(broadcastIntent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
