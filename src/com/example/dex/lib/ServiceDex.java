package com.example.dex.lib;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class ServiceDex extends Service {
	private static final String TAG = "ServiceDex";
    private static final boolean DEBUG = true;

    private static final String format = "yyyy-MM-dd HH:mm:ss";
    
    private static ServiceDex mInstance = null;
    private Looper mServiceLooper;
    private static ServiceHandler mServiceHandler;

    private static final int EVENT_QUIT = 5000;
    private static final int EVENT_STOP_SESSION = 5001;
    private static final int EVENT_DO = 5002;
    private static final int EVENT_TIME_OUT = 5003;
    
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
            if (DEBUG) Log.v(TAG, "Enter ServiceHandler(), looper = " + looper);
        }

        public void handleMessage(Message msg) {
            if (DEBUG) Log.v(TAG, "Enter handleMessage(), msg = " + msg);
            switch (msg.what) {
            case EVENT_QUIT :
                getLooper().quit();
                break;

            case EVENT_STOP_SESSION :
            	stopSelf();
            	break;
            	
            case EVENT_DO :
            	Toast.makeText(mInstance,
                        "Service Started In Dex!!!",
                        Toast.LENGTH_LONG).show();
            	sendEmptyMessageDelayed(EVENT_STOP_SESSION, 10000);
            	break;
            	
            case EVENT_TIME_OUT :
            	//int iTaskCount = msg.arg1;
            	if (DEBUG) Log.d(TAG, "EVENT_TIME_OUT");
            	break;
            	
            default :
                Log.w(TAG, "msg.what=" + msg.what);
                return;
            }
        }
    }
    
    @Override
    public void onCreate() {
    	if (DEBUG) Log.v(TAG, "Enter onCreate()");
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceTemplate",
                		Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mInstance = this;
	    if (DEBUG) Log.d(TAG, "Leave onCreate()");
    }
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		if (DEBUG) Log.v(TAG, "onStartCommand(), flags = " + flags + 
				", startId=" + startId + ", intent=" + intent);
        Message msg = mServiceHandler.obtainMessage(EVENT_DO);
        msg.arg1 = startId;
        if (mServiceHandler.getLooper().getThread().isAlive()) { 
        	mServiceHandler.sendMessage(msg);
        }
        return START_NOT_STICKY;
    }

	@Override
    public void onDestroy() {
        if (DEBUG) Log.v(TAG, "Enter onDestroy()");
        mInstance = null;
        mServiceHandler.sendEmptyMessage(EVENT_QUIT);
    }
	
	private String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String timeStamp = sdf.format(new Date(System.currentTimeMillis()));
		if (DEBUG) Log.i(TAG, "timeStamp = " + timeStamp);
		//return "2013-01-09 21:20:39";
		return timeStamp;
	}

    public static void start(Context context) {
    	if (DEBUG) Log.v(TAG, "Enter start(), context = " + context);
		Intent intent = new Intent(context, ServiceDex.class);
		context.startService(intent);
		if (DEBUG) Log.v(TAG, "Leave start()");
	}

    public static void stop() {
    	if (DEBUG) Log.v(TAG, "Enter stop()");
    	if (!isServiceWorked("com.example.dex.lib.ServiceDex", mInstance)) {
    		if (DEBUG) Log.d(TAG, "ServiceDex Service is not running, just return");
    		return;
    	}
		mServiceHandler.sendEmptyMessage(EVENT_STOP_SESSION);
		if (DEBUG) Log.v(TAG, "Leave stop()");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
  	private static boolean isServiceWorked(String serString, Context context) {
		ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningService) {
			if (runningServiceInfo.service.getClassName().toString().equals(serString)) {
				return true;
			}
		}
		return false;
	}
}