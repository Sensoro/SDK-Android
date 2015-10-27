How to use SENSORO SDK in Android 6.0?
=================

>  Author: Sensoro
>
>  Version: 1.0
>  
>  Date: 2015-10-27 18:53:00

### Background

In Android 6.0, the BLE scanning is associated with Location Service. Found in out test, if you want to get BLE scanning result, you must request permission `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION`.

While Google also released a new permissions model `Runtime Permissions` in Android 6.0. In the model, permissions must be granted by user during the App is running. So there are some difference using SENSORO SDK in Android 6.0.

*Notice：You must open the Location Service in Android 6.0 so that you could get BLE scanning results during the App is running.*

### How to Use

> If your App targetSdkVersion is 23 and above, you need do somethings below.

1.Declare permission in `AndroidManifest.xml` as below:

```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
```

2.Add sample codes below in the first `Activity` of App:：

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
				// functionality that depends on this permission. Do the task you need to do.
				Log.v(TAG,"permission was granted");
				// your code
			} else {
				// permission denied, boo! Disable the functionality that depends on this permission.
				Log.v(TAG,"permission denied");
			}
		}
		// other 'case' lines to check for other permissions this App might request
	}
}

public void requirePermission(){
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
    	// your code
	} else {
		// Should we show an explanation?
		if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_COARSE_LOCATION)) {
				// Show an expanation to the user *asynchronously* -- don't block this thread 
				// waiting for the user's response! After the user sees the explanation, try 
				// again to request the permission.
				Log.v(TAG, "Show an expanation to the user *asynchronously*");
			} else {
				// No explanation needed, we can request the permission.
				Log.v(TAG,"// No explanation needed, we can request the permission.");
				ActivityCompat.requestPermissions(this,new String[]{
						Manifest.permission.ACCESS_COARSE_LOCATION},
						MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
			}
		} else {
			// your code
		}
	}
}
...
```
