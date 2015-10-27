SENSORO SDK 在 Android 6.0 中的使用
=================

### 背景介绍
在 Android 6.0 系统中，Google 将 BLE 的设备扫描与位置服务相关联。经测试，在开发中过程中，如果想要成功检测到 BLE 设备，必须申请访问位置服务权限 `ACCESS_FINE_LOCATION` 或者 `ACCESS_COARSE_LOCATION`。

而在 Android 6.0 系统中， 新的权限机制 `Runtime Permissions` 需要在 APP 运行时获得权限，因此在使用 SDK 的过程中，APP 需要动态申请上述权限来保证正常运行。

*说明：APP 在运行过程中，必须保持定位开启，才能成功扫描到 BLE 设备。*

### 使用方法

> 以下的方法适用于 targetSdkVersion >= 23 的 APP，targetSdkVersion < 23 的 APP 可以暂不用考虑，但是必须开启位置服务才能扫描到设备

在 AndroidManifest.xml 文件中添加权限申请：

```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

在 APP 启动的第一个 Acitivy 中 `onCreate` 函数中添加以下代码：

```
...
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

    requirePermission();
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	switch (requestCode) {
		case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// 用户授权成功
				Log.v(TAG,"permission was granted");
				// 业务逻辑
			} else {
				// 申请权限被用户拒绝，相关功能不可使用
				Log.v(TAG,"permission denied");
			}
		}
		// 其他 'case' 可能是 app 申请的其他权限
	}
}

public void requirePermission(){
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
    	// 业务逻辑
	} else {
		// 是否应该给用户提示说明？
		if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_COARSE_LOCATION)) {
				// 异步方式提示用户，不要阻塞在这等待用户响应。当用户看到提示之后，尝试重新获取权限
				Log.v(TAG, "Show an expanation to the user *asynchronously*");
			} else {
				// 不需要提示说明，直接获取权限
				Log.v(TAG,"// No explanation needed, we can request the permission.");
				ActivityCompat.requestPermissions(this,new String[]{
						Manifest.permission.ACCESS_COARSE_LOCATION},
						MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
			}
		} else {
			// 业务逻辑
		}
	}
}
...
```