# Android SDK
=====
Yunzi and Sensoro Tag are a kind of wireless intelligent sensor integrated with iBeacon function. 

**Through employing our Android SDK, mainly you will achieve functions below: **

1. Range nearby sensory devices
2. Fetch configurations of the devices
3. Upload devices’ status to  <a href="http://cloud.sensoro.com" target="_blank">SENSORO Cloud</a>
4. Modify device configurations

###Install SDK
#####1. Download
You may download the latest Android SDK from following link：
  
[Download address](https://github.com/Sensoro/SDK-Android)

#####2. Installation and configuration

**Integrate SDK .jar file**


	Build a new libs fold at root directory and unarchive the downloaded SDK to the folder.


The SDK includes following modules：

- sensorocloud-<Version>.jar
- sensorobeaconkit-<Version>.jar
- android-async-http-1.4.6.jar
- greendao-1.3.7.jar
- gson-2.3.1.jar



**Modify AndroidManifest.xml**

1. Add uses-permission to AndroidManifest.xml

```	
<manifest
	...
	<uses-permission android:name="android.permission.BLUETOOTH" />
  	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
   	<uses-permission android:name="android.permission.INTERNET" />
   	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	...
</manifest>
```

2. Add service to AndroidManifest.xml 

```
<manifest
	...
	<application
		...
		<service android:name="com.sensoro.beacon.kit.BeaconProcessService"/>
		<service android:name="com.sensoro.beacon.kit.BeaconService"/>
		<service android:name="com.sensoro.beacon.kit.IntentProcessorService"/>
		...
	</application>
	...
</manifest>
```

###Get started
##### I. Initialize the App

Firstly you need to initialize SDK，and setup whether it will upload sensor data (battery status, UMM), etc. Then start ranging. Sample code of enable the SDK is as following:

```
SensoroManager sensoroManager = SensoroManager.getInstance(context);
/**
 * Check whether the Bluetooth is on
 **/
if (sensoroManager.isBluetoothEnabled()) {
	/**
	 * Enable cloud service (upload sensor data, including battery status, UMM, etc.)。Without setup, it keeps in closed status as default.
	 **/
	sensoroManager.setCloudServiceEnable(true);
	/**
	 * Enable SDK service
	 **/
	try {
	    sensoroManager.startService();
	} catch (Exception e) {
   	 e.printStackTrace(); // Fetch abnormal info
	}
}
```

**Tips：**

- SDK works with Bluetooth 4.0. Check on whether the Bluetooth is on before enabling the SDK. It will not work properly otherwise. 
- The design of the SDK adopts singleton pattern. It is recommended to initialize it at the inherited **Application**. 

If the Bluetooth of the Android device is off, please use following code to request switching one the Bluetooth：

```
Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
```


##### II. Setup sensor data monitoring

You may detect the presence of Beacon by employing and setting-up BeaconManagerListener，Sample code:

```
BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {

    @Override
    public void onUpdateBeacon(ArrayList<Beacon> beacons) {
        // Refresh sensor info                  
    }
    
    @Override
    public void onNewBeacon(Beacon beacon) {
        // New sensor found        
    }
    
    @Override
    public void onGoneBeacon(Beacon beacon) {
        // A sensor disappears from the range     
    }
};
sensoroManager.setBeaconManagerListener(beaconManagerListener);
```
The refresh frequency of sensor info here is 1 time per second; when a new sensor is found, a return of sensor's disappearing will be sent if this specific device is not found in range in 8 second. 

**Tips：**

- Please setup before enabling the SDK.

##### III. Monitor the whether the device is in range

The presence or disappearance of the device usually triggers certain operations. Code below will be used to determine whether Yunzi with SN "0117C5456A36" is entering or leaving the range:

```
BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {

    @Override
    public void onUpdateBeacon(ArrayList<Beacon> beacons) {
        // Refresh sensor info                  
    }
    
    @Override
    public void onNewBeacon(Beacon beacon) {
        if (beacon.getSerialNumber().equals("0117C5456A36")){
        	// Yunzi with SN "0117C5456A36" enters the range  
        }       
    }
    
    @Override
    public void onGoneBeacon(Beacon beacon) {
        if (beacon.getSerialNumber().equals("0117C5456A36")){
        	// Yunzi with SN "0117C5456A36" leaves the range
        }      
    }
};
sensoroManager.setBeaconManagerListener(beaconManagerListener);
```

** Tips：**

- The callback function is run in a non-UI thread. Please do not perform any UI related operations in the callback function, otherwise it may cause an exception running of the SDK. Please excute your code with Handler or Activity.runOnUiThread if necessary. 

##### IV. Fetch and refresh sensor data

Table of Yunzi's significant parameters

| Attribute           | Description                                    |
| :------------------ | :-------------------------------------------   |
| serialNumber        | SN, Unique identity attribute of the device    |
| major               | Major in iBeacon protocol                      |
| minor               | Minor in iBeacon protocol                      |
| proximityUUID       | UUID in iBeacon protocol                       |
| rssi                | Signal intensity                               |
| accuracy            | Distance (meter)                               |
| proximity           | Proximity levels(far, near, immediate, unknown)|
| temperature         | Chip temperature                               |
| light               | Light                                          |
| movingState         | Motion state                                   |
| accelerometerCount  | Motion counter                                 |
| batteryLevel        | Battery status                                 |
| hardwareModelName   | Hardware model                                 |
| firmwareVersion     | Firmware version                               |
| measuredPower       | Signal intensity 1 meter distance from device  |
| transmitPower       | Transmitter power                              |
| advertisingInterval | Advertising interval                           |

The App will be triggered to execute certain operations by changes in sensor's data status, including changes in signal intensity, light, motion state, counter value. You can range designated device and check on its status change via transversing the refreshed device list. Following sample code is used for judging whether there is any change in the motion state of Yunzi with SN "0117C5456A36":                                                                                                                           

```
BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {

    @Override
    public void onUpdateBeacon(final ArrayList<Beacon> beacons) {
    	// Check whether there is motion state change in Yunzi with SN "0117C5456A36" 
    	for(Beacon beacon:beacons){
        	if (beacon.getSerialNumber().equals("0117C5456A36")){
           		if (beacon.getMovingState() == Beacon.MovingState.DISABLED){
            		// Disable accelerometer
            	} else if (beacon.getMovingState() == Beacon.MovingState.STILL){ 
            		// Device is at static
            	} else if (beacon.getMovingState() == Beacon.MovingState.MOVING){
            		// Device is moving
            	}
            }
    	}
    }
    
    @Override
    public void onNewBeacon(Beacon beacon) {
        // New device found in range        
    }
    
    @Override
    public void onGoneBeacon(Beacon beacon) {
        // A device has left the range     
    }
};
```

** Tips：**

- The callback function is run in a non-UI thread. Please do not perform any UI related operations in the callback function, otherwise it may cause an exception running of the SDK. Please excute your code with Handler or Activity.runOnUiThread if necessary. 

##### V. 'Prevent squatters' function of Yunzi/Tag

You will be able to enable 'Prevent squatters' function of Yunzi, claiming your ownership of this device and preventing a third party to check and usurp it. A key is required to enable this function. You may apply for the key after signing up [SENSORO Cloud](https://cloud.sensoro.com) and embed the key into the SDK. Sample code: 

```
/**
 * Setup key for preventing squatters (if applicable)
 **/
sensoroManager.addBroadcastKey("7b4b5ff594fdaf8f9fc7f2b494e400016f461205");
```

**Tips: **

- Please setup before enabling the SDK
- If the SDK is not capable to range the device, please check whether the key used for the device is in accordance with the setup in the SDK. 


###Conclusion
So far, you have accomplished all procedures integrating SDK to your project. With the aid of our SDK, interactions between App and Beacon can be easily achieved. For more technical details, please refer to complete <a href="http://sensoro.github.io/download/sdk/android/doc/index.html" target="_blank">SDK documents</a>, and our <a href="https://github.com/Sensoro/Yunzi-Android" target="_blank">Demo's source code</a>.



