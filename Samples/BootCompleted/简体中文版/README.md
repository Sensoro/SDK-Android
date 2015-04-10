# 开机启动 Sensoro SDK

> 作者：Sensoro
>
>  版本：v1.0 
>  
>  时间：2015年04月10日14:44:53


根据业务需求，可能需要在 Android 设备开机时，启动 SDK 来监听周边的 iBeacon。

### 开机广播监听

在 Android 系统中，开机启动需要监听系统开机广播 `android.intent.action.BOOT_COMPLETED`。

1.实现接收器监听开机广播

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

2.在 AndroidManifest.xml 中注册广播接收器

```
<receiver android:name="com.sensoro.bootcompleted.BootCompletedBroadcastReceiver">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <category android:name="android.intent.category.HOME" />
    </intent-filter>
</receiver>
```

接收到开机广播后，可以通过 Activity 或者后台 Service 来启动 SDK。

### Activity 中启动 SDK

接收到开机广播之后，可以直接启动 APP 的 Activity，此时可以在 Activity 的 onCreate 函数中，启动 SDK。

```
if (application.isBluetoothEnabled()){
	application.startSensoroSDK();
} 
```

如果蓝牙没有开启，在 Activity 中有两种方式开启蓝牙：

- 请求蓝牙权限开启蓝牙
- 后台静默开启蓝牙

#### 请求蓝牙权限开启蓝牙

通过 Intent 请求，获得用户允许之后打开蓝牙

```
Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
```

在`onActivityResult`回调中启动 SDK

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
		application.startSensoroSDK();
	}
}
```

#### 后台静默开启蓝牙

通过蓝牙 API，不经用户允许，直接打开蓝牙

```
BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
bluetoothAdapter.enable();
```
在蓝牙广播接收器中，启动 SDK

class BluetoothBroadcastReceiver extends BroadcastReceiver {

```
@Override
public void onReceive(Context context, Intent intent) {
	if (intent.getAction().equals(Constant.BLE_STATE_CHANGED_ACTION)){
		if (application.isBluetoothEnabled()){
			application.startSensoroSDK();
		}
	}
}
```

### Service 中启动 SDK

接收到开机广播之后，可以直接启动 APP 的 Service，在 Service 的 onCreate 函数中，启动 SDK。</br>

```
if (application.isBluetoothEnabled()){
	application.startSensoroSDK();
} 
```

如果蓝牙没有开启，在 Service 中开启蓝牙的方式只有一种：后台静默开启蓝牙。具体操作方式与 Activity 的操作相同。

### 建议

1. 没有特殊需求，尽量避免监听开机广播，随系统自启动 APP。
2. 如果用户默认蓝牙关闭，尽量通过用户授权打开蓝牙，不推荐使用后台静默方式开启蓝牙。

### 附录

由于 Android 系统差异化原因，不保证以上方式适用于所有 Android 机型。</br>
下面列举了已经测试过的 Android 机型：

| 型号           | 版本           |
| ------------- |:-------------:|
| Nexus 5       | 5.0.1         |
| Nexus 5       | 5.1           |