package com.sensoro.experience.tool;

import com.sensoro.cloud.SensoroManager;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {

	private static final String TAG = MyApp.class.getSimpleName();
	SensoroManager sensoroManager;

	@Override
	public void onCreate() {
		initSensoro();
		super.onCreate();
	}

	private void initSensoro() {
		sensoroManager = SensoroManager.getInstance(getApplicationContext());
		sensoroManager.setCloudServiceEnable(false);
		try {
			sensoroManager.startService();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onTerminate() {
		if (sensoroManager != null) {
			sensoroManager.stopService();
		}
		super.onTerminate();
	}

}
