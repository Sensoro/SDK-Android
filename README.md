SDK for  Android
=======================

The SDK for Android is a library that allows interaction with [Yunzi](http://www.sensoro.com/). 

## Installation

### 1. Import *sensorobeaconkit.jar*

Copy  *sensorobeaconkit.jar* to your **libs** directory. If your project does not exist **libs** folder, make it in root directory. 

### 2. Declar in *AndroidManifest.xml*

You should add following permissions and services declaration to your *AndroidManifest.xml*:

```xml
<manifest
    ...
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    ...
    <application
        ...
        <service android:name="com.sensoro.beacon.kit.BeaconProcessService"></service>
        <service android:name="com.sensoro.beacon.kit.BeaconService"></service>
        ...
    </application>
    ...
</manifest>

```

## Docs

- [Current JavaDoc Documentation](http://static.sensoro.com/documents/SBK/Android/index.html)
- [Community Portal of Sensoro](https://sensoro.zendesk.com/hc/communities/public/questions?locale=en-us)
- [User Guide](http://www.sensoro.com/docs/)

## FAQ

Please see [FAQ](https://sensoro.zendesk.com/hc/en-us) in [Sensoro](http://www.sensoro.com/).

## Changelog

- 2.1.3
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0, B0-2.1, B0-2.2, B0-2.3.`
    - Add ENUM `SecureBroadcastInterval`.
    - Change API `writeSecureBroadcastRotationInterval(int)` to `writeSecureBroadcastInterval(SecureBroadcastInterval)`.
- 2.1.2
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0, B0-2.1, B0-2.2, B0-2.3.`
    - Add API secure broadcast - `writeSecureBroadcastRotationInterval`.
    - Fix calculate accelerometer count bug.
- 2.1.1
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0, B0-2.1, B0-2.2.`
    - **Important upgrade**: Upgrade APIs. For more [click](https://github.com/sensoro/SBK-Android/releases/tag/v2.1.1) here.
    - Fix *ConcurrentModificationException* bug.
- 2.1
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0, B0-2.1, B0-2.2.`
    - Improve the JavaDoc of SDK.
    - Add the version of SDK.
- 2.0
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0, B0-2.1, B0-2.2.`
    - Change Java annotation of SDK from Chinese to English.
    - Support that the hardware of Yunzi is above 2.0.
    - Throw exception when calls `startService`.
- 1.1
    - `Support A0-1.0, B0-1.0, A0-2.0, B0-2.0.`
    - Support that the hardware of Yunzi is 2.0.
    - Add a value ` ADVERTISING_INTERVAL_1285` of *AdvertisingInterval*.
    - Fix some bugs.
- 1.0
    - `Support A0-1.0, B0-1.0.`
    - Initial version.
