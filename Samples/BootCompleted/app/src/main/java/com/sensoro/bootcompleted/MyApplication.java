package com.sensoro.bootcompleted;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import java.util.ArrayList;

/**
 * Created by Sensoro on 15/3/11.
 */
public class MyApplication extends Application implements BeaconManagerListener{
    private static final String TAG = MyApplication.class.getSimpleName();
    private SensoroManager sensoroManager;

    @Override
    public void onCreate() {
        super.onCreate();

        initSensoroSDK();

        /**
         * Start SDK in Service.
         */
        Intent intent = new Intent();
        intent.setClass(this,MyService.class);
        startService(intent);
    }

    /**
     * Initial Sensoro SDK.
     */
    private void initSensoroSDK() {
        sensoroManager = SensoroManager.getInstance(getApplicationContext());
        sensoroManager.setCloudServiceEnable(true);
        sensoroManager.addBroadcastKey("7b4b5ff594fdaf8f9fc7f2b494e400016f461205");
        sensoroManager.setBeaconManagerListener(this);
    }

    /**
     * Start Sensoro SDK.
     */
    public void startSensoroSDK() {
        try {
            sensoroManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether bluetooth enabled.
     * @return
     */
    public boolean isBluetoothEnabled(){
        return sensoroManager.isBluetoothEnabled();
    }

    @Override
    public void onNewBeacon(Beacon beacon) {
        /**
         * Check whether SDK started in logs.
         */
        Log.v(TAG,beacon.getSerialNumber());
    }

    @Override
    public void onGoneBeacon(Beacon beacon) {

    }

    @Override
    public void onUpdateBeacon(ArrayList<Beacon> arrayList) {

    }
}
