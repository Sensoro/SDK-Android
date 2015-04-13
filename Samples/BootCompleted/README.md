# How to start Sensoro SDK on setup

>  Author：Sensoro
>
>  Version：v1.0 
>  
>  Date：2015/04/13/18:40:21

In some cases, it needs to start SDK to monitor iBeacons when Android devices set up.

### Listen BroacastReceiver on setup

1.Implement a BroacastReceiver to listen boot completed broadcast `android.intent.action.BOOT_COMPLETED` when devices set up.

```
public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    static final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED_ACTION)) {
            /**
             * Start Sensoro SDK in Activity with boot.
             */
//            Intent bootCompletedActivityIntent = new Intent(context, MainActivity.class);
//            bootCompletedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(bootCompletedActivityIntent);

            /**
             * Startan Sensoro SDK in Service with boot.
             */
            Intent bootCompletedSerivceIntent = new Intent(context, MyService.class);
            context.startService(bootCompletedSerivceIntent);
        }
    }
}
```

2.Register a broadcast receiver in `AndroidManifest.xml`.

```
<receiver android:name="com.sensoro.bootcompleted.BootCompletedBroadcastReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <category android:name="android.intent.category.HOME" />
    </intent-filter>
</receiver>
```

After receiving boot completed broadcast, you can start SDK in Activity or in Background Service.

### Start SDK in Activity

After receiving boot completed broadcast, you can start an Activity of App. Start SDK in function `onCreate` of Activity.

```
if (application.isBluetoothEnabled()){
    application.startSensoroSDK();
} 
```

If the bluetooth is disabled, there are two ways to enable it:

- Require bluetooth permission to enbale.
- Enable bluetooth in background in silence.


#### Require bluetooth permission to enbale

Require permission to enable bluetooth with `Intent`.

```
Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
```

Start SDK in callback of `onActivityResult`.

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
        application.startSensoroSDK();
    }
}
```

#### Enbale bluetooh in background in silence

Enable bluetooth through Android API without permission.

```
BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
bluetoothAdapter.enable();
```

Start SDK in bluetooth BroadcastReceiver.

```
class BluetoothBroadcastReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Constant.BLE_STATE_CHANGED_ACTION)){
        if (application.isBluetoothEnabled()){
            application.startSensoroSDK();
        }
    }
}
```

### Start SDK in Background Service

After receiving boot completed broadcast, you can start a Background Service, and start SDK in function `onCreate` of Service.

```
if (application.isBluetoothEnabled()){
    application.startSensoroSDK();
} 
```

If the bluetooth is disabled, there is the only one way to enbale it in Service: Enbale bluetooh in background in silence. The sample code is the same with Activity above.

### Suggestion

1. Avoid to listen boot completed broadcast to start app when devices set up if there are no special needs.
2. If the bluetooth is disabled in default, it is batter to require permission to enable it than enable it in background in silence.

### Appendix

Because of Android fragmentation, it is not compatible with all Android devices.</br>
Tested Android Devices List:

| Model         | Version       |
| ------------- |:-------------:|
| Nexus 5       | 5.0.1         |
| Nexus 5       | 5.1           |