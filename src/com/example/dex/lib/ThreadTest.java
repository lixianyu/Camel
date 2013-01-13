package com.example.dex.lib;

import android.util.Log;

public class ThreadTest {
	public static final String TAG = "ThreadTest";
	
	
	public void testThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 0;
				while (true) {
					Log.i(TAG, "i = " + i++);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (i > 20) break;
				}
				
			}
			
		}).start();
	}
}
