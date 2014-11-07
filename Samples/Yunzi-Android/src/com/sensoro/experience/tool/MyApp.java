package com.sensoro.experience.tool;

import com.sensoro.beacon.kit.SensoroBeaconManager;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {

	private static final String TAG = MyApp.class.getSimpleName();
	SensoroBeaconManager sensoroBeaconManager;

	@Override
	public void onCreate() {
		initSensoro();
		super.onCreate();
	}

	private void initSensoro() {
		sensoroBeaconManager = SensoroBeaconManager.getInstance(getApplicationContext());
		try {
			sensoroBeaconManager.startService();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void onTerminate() {
		if (sensoroBeaconManager != null) {
			sensoroBeaconManager.stopService();
		}
		super.onTerminate();
	}

}
