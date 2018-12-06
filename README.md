**Warning** This SDK only support android 7.0 and less. 

Android SDK
=================
Yunzi and Sensoro Tag are a kind of wireless intelligent sensor integrated with iBeacon function. 

**Through employing our Android SDK, mainly you will achieve functions below:**

1. Range nearby sensory devices
2. Fetch configurations of the devices
3. Upload devices’ status to  <a href="http://cloud.sensoro.com" target="_blank">SENSORO Cloud</a>
4. Modify device configurations

### Install SDK
##### 1. Download
You may download the latest Android SDK from following link：
  
[Download address](https://github.com/Sensoro/SDK-Android)

##### 2. Installation and configuration

**Integrate SDK .jar file**


	Build a new libs fold at root directory and unarchive the downloaded SDK to the folder.


The SDK includes following modules：

- sensoro-scanner-\<Version\>.jar
- sensorocloud-\<Version\>.jar
- sensorobeaconkit-\<Version\>.jar
- //添加gson包
- compile 'com.google.code.gson:gson:2.8.5'
- //网络请求包
- compile 'com.loopj.android:android-async-http:1.4.9'
- compile 'org.apache.httpcomponents:httpcore:4.4.1'

**Modify AndroidManifest.xml**

1. Add uses-permission to AndroidManifest.xml

```	
<manifest
	...
	<uses-permission android:name="android.permission.BLUETOOTH" />
  	<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
   	<uses-permission android:name="android.permission.INTERNET" />
   	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
   	<uses-permission android:name="android.permission.WAKE_LOCK" />
	...
</manifest>
```

2. Add service to AndroidManifest.xml 

```
<manifest
	...
	<application
		...
		<service android:name="com.sensoro.beacon.kit.BeaconService"/>
		<service android:name="com.sensoro.beacon.kit.IntentProcessorService"/>
		...
	</application>
	...
</manifest>
```

### Get started
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

**Tips：**

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

**Tips：**

- The callback function is run in a non-UI thread. Please do not perform any UI related operations in the callback function, otherwise it may cause an exception running of the SDK. Please excute your code with Handler or Activity.runOnUiThread if necessary. 

##### V. 'Prevent squatters' function of Yunzi/Tag

You will be able to enable 'Prevent squatters' function of Yunzi, claiming your ownership of this device and preventing a third party to check and usurp it. A key is required to enable this function. You may apply for the key after signing up [SENSORO Cloud](https://cloud.sensoro.com) and embed the key into the SDK. Sample code: 

```
/**
 * Setup key for preventing squatters (if applicable)
 **/
sensoroManager.addBroadcastKey("01Y2GLh1yw3+6Aq0RsnOQ8xNvXTnDUTTLE937Yedd/DnlcV0ixCWo7JQ+VEWRSya80yea6u5aWgnW1ACjKNzFnig==");
```

**Tips：**

- Please setup before enabling the SDK
- If the SDK is not capable to range the device, please check whether the key used for the device is in accordance with the setup in the SDK. 


###Conclusion
So far, you have accomplished all procedures integrating SDK to your project. With the aid of our SDK, interactions between App and Beacon can be easily achieved. For more technical details, please refer to complete <a href="http://sensoro.github.io/download/sdk/android/doc/index.html" target="_blank">SDK documents</a>, and our <a href="https://github.com/Sensoro/Yunzi-Android" target="_blank">Demo's source code</a>.

### Appendices

##### Sensoro Beacon Power Specification

<table>
	<tr>
  		<th rowspan="2">LEVEL/MODEL</th>
  		<th colspan="2">A0</th>
		<th colspan="2">B0</th>
		<th colspan="2">C0</th>
	</tr>
	<tr align="center">
		<td>RSSI</td>
		<td>Range</td>
		<td>RSSI</td>
		<td>Range</td>
		<td>RSSI</td>
		<td>Range</td>
	</tr>
	<tr align="center">
		<td>LEVEL0</td>
		<td>-23 dbm</td>
		<td>~2 m</td>
		<td>-30 dbm</td>
		<td>~2 m</td>
		<td>Micro -30 dbm</td>
		<td>~5 cm</td>
	</tr>
	<tr align="center">
		<td>LEVEL1</td>
		<td>-6 dbm</td>
		<td>~7 m</td>
		<td>-20 dbm</td>
		<td>~7 m</td>
		<td>Micro -20 dbm</td>
		<td>~50 cm</td>
	</tr>
	<tr align="center">
		<td>LEVEL2</td>
		<td>0 dbm</td>
		<td>~15 m</td>
		<td>-16 dbm</td>
		<td>~10 m</td>
		<td>Micro -16 dbm</td>
		<td>~80 cm</td>
	</tr>
	<tr align="center">
		<td>LEVEL3</td>
		<td>-</td>
		<td>-</td>
		<td>-12 dbm</td>
		<td>~15 m</td>
		<td>Micro -12 dbm</td>
		<td>~1.5 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL4</td>
		<td>-</td>
		<td>-</td>
		<td>-8 dbm</td>
		<td>~22 m</td>
		<td>-30 dbm</td>
		<td>~2 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL5</td>
		<td>-</td>
		<td>-</td>
		<td>-4 dbm</td>
		<td>~27 m</td>
		<td>-20 dbm</td>
		<td>~7 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL6</td>
		<td>-</td>
		<td>-</td>
		<td>0 dbm</td>
		<td>~50 m</td>
		<td>-16 dbm</td>
		<td>~15 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL7</td>
		<td>-</td>
		<td>-</td>
		<td>+4 dbm</td>
		<td>~90 m</td>
		<td>-12 dbm</td>
		<td>~20 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL8</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-8 dbm</td>
		<td>~25 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL9</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-4 dbm</td>
		<td>~45 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL10</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>0 dbm</td>
		<td>~70 m</td>
	</tr>
	<tr align="center">
		<td>LEVEL11</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>-</td>
		<td>+4 dbm</td>
		<td>~100 m</td>
	</tr>
</table>


##### Android-SDK 4.0  Beacon Configuration ResultCode Specification

> Please use  *SensoroBeaconConnectionV4* and *BeaconConfiguration*  to config beacons since Android-SDK 4.0. Please refer to *resultCode* of callback as below.

| HEX			| DEC			| Description	|
| :-----------: |:-------------:| :------------:|
| 0x0000		| 0				| Success		|
| 0x0100     	| 256     		| MCU Busy		|
| 0x0200		| 512      		| LED Busy		|
| 0x0300		| 768      		| No Permission	|
| 0x0400		| 1024      	| Invalid Configuration Command	|
| 0x0500		| 1280      	| Params Length Invalid			|
| 0x0501		| 1281      	| Power Length Invalid			|
| 0x0502		| 1282      	| Adv Interval Length Invalid	|
| 0x0503		| 1283      	| Mrssi Length Invalid			|
| 0x0504		| 1284      	| Energy Saving Mode Length Invalid	|
| 0x0505		| 1285      	| Password Length Invalid			|
| 0x0506		| 1286      	| Work Indicator Length Invalid		|
| 0x0511		| 1297      	| iBeacon Switch Length Invalid		|
| 0x0512		| 1298      	| UUID Length Invalid				|
| 0x0513		| 1299      	| Major Length Invalid				|
| 0x0514		| 1300      	| Minor Length Invalid				|
| 0x0515		| 1301      	| Broadcast Rotate Interval Length Invalid	|
| 0x0516		| 1302      	| Background Enhancement Length Invalid		|
| 0x0517		| 1303      	| Broadcast Key Length Invalid				|
| 0x0521		| 1313      	| Temp Sampling Interval Length Invalid		|
| 0x0522		| 1314      	| Light Sampling Interval Length Invalid	|
| 0x0523		| 1315      	| Accelerometer Sensitivity Length Invalid	|
| 0x0531		| 1329      	| UID Switch Length Invalid					|
| 0x0532		| 1330      	| URL Switch Length Invalid					|	
| 0x0533		| 1331      	| TLM Switch Length Invalid					|
| 0x0534		| 1332      	| UID Length Invalid (NID)					|
| 0x0535		| 1333      	| UID Length Invalid (BID)					|
| 0x0536		| 1334      	| URL Length Invalid						|
| 0x0537		| 1335      	| TLM Interval Length Invalid				|
| 0x0541		| 1345      	| AliBeacon Switch Length Invalid			|
| 0x0600		| 1536      	| Params Format Invalid						|
| 0x0601		| 1537      	| Power Format Invalid						|
| 0x0602		| 1538      	| Adv Interval Format Invalid				|	
| 0x0603		| 1539      	| Mrssi Format Invalid						|
| 0x0604		| 1540      	| Energy Saving Mode Format Invalid			|
| 0x0605		| 1541      	| Password Format Invalid					|
| 0x0606		| 1542      	| Work Indicator Format Invalid				|
| 0x0611		| 1553      	| iBeacon Switch Format Invalid				|	
| 0x0612		| 1554      	| UUID Format Invalid						|
| 0x0613		| 1555      	| Major Format Invalid						|
| 0x0614		| 1556      	| Minor Format Invalid						|
| 0x0615		| 1557      	| Broadcast Rotate Interval Format Invalid	|
| 0x0616		| 1558      	| Background Enhancement Format Invalid		|
| 0x0617		| 1559      	| Broadcast Key Format Invalid				|
| 0x0621		| 1569      	| Temp Sampling Interval Format Invalid		|
| 0x0622		| 1570      	| Light Sampling Interval Format Invalid	|
| 0x0623		| 1571      	| Accelerometer Sensitivity Format Invalid	|
| 0x0631		| 1585      	| UID Switch Format Invalid					|
| 0x0632		| 1586      	| URL Switch Format Invalid 				|	
| 0x0633		| 1587      	| TLM Switch Format Invalid					|
| 0x0634		| 1588      	| UID Format Invalid (NID)					|
| 0x0635		| 1589      	| UID Format Invalid (BID)					|
| 0x0636		| 1590      	| URL Format Invalid						|
| 0x0637		| 1591      	| TLM Interval Format Invalid				|
| 0x0641		| 1601      	| AliBeacon Switch Format Invalid			|
| 0x0700		| 1792      	| Invalid Configuration Params				|		
| 0x0800		| 2048      	| Protocol Version Error					|		
| 0x8000		| 32768      	| Bluetooth Error							|
| 0x8100		| 33024      	| Bluetooth Communication Error				|
| 0x8200		| 33280      	| Bluetooth Connection Timeout				|
| 0x8300		| 33536      	| Not Support								|


