# Android SDK 省电模式及功耗

> 作者：Sensoro
> 
> 版本：v1.0
> 
> 日期：2015年05月15日



## 为什么需要省电模式

iBeacon 工作原理是通过 2.4GHz 信号，周期性发射广播数据。Android 设备若要接收 iBeacon 数据，就需要不停的进行蓝牙扫描操作。对于 Android 设备而言，蓝牙扫描的操作是很相对比较耗电的。如果持续对 iBeacon 广播进行监听，会加快电池电量的消耗。对于部分用户来说，一个 APP 十分耗电是不可以接受的。

## 省电模式工作原理

如果当 APP 切换到后台时，说明用户对于该 APP 的需求变的很低，但为了达到某些用户体验的效果，又不能停止对 iBeacon 的数据监测。因此，可以适当延长 Android 设备的蓝牙扫描间隔，既能满足 APP 需求，又能达到省电的效果。

## 省电模式的使用

### 启用省电模式

在 Android 代码的 Application 中，实现 BackgroundPowerSaver 对象，即可开启省电模式。

```
import com.sensoro.cloud.BackgroundPowerSaver;

public class MyApplication extends Application {
    private BackgroundPowerSaver backgroundPowerSaver;

    public void onCreate() {
        super.onCreate();
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }
}
```

### 设置省电模式参数

用户可以根据自己的需求，调整前台和后台的扫描时间和扫描间隔，以达到自己省电的需求。示例代码如下：

```
// set the foreground duration of the scan to be 1.1 seconds
sensoroManager.setForegroundScanPeriod(1100);
// set the foreground time between each scan to be 0
sensoroManager.setForegroundBetweenScanPeriod(0);
// set the background duration of the scan to be 5 seconds
sensoroManager.setBackgroundScanPeriod(5000);
// set the background time between each scan to be 30 seconds
sensoroManager.setBackgroundBetweenScanPeriod(30000);
```
如果不进行任何设置，前台默认扫描时间为 1100 ms，前台默认扫描间隔为 0 ms；
后台默认扫描时间为 5000 ms，后台默认扫描间隔为 30000 ms。

省电模式参数与功耗的关系：扫描时间越长，功耗越高；扫描间隔越短，功耗越高。

## 省电模式实测效果

### 测试环境

- 电源：安泰信 APS3005D 稳压电源
- 万用表：VICTOR 86D(带 USB 输出)
- 手机：Nexus 5，三星 S4

### 测试方法

1. 将电源、万用表、手机串联
2. 设置稳压电源为 4.5V。手机默认电压为 3.8V，因为万用表有内阻，所以需要适当将电源调高，否则设备无法启动
3. 将万用表连接至 PC，安装DMM Data Precessor [http://www.china-victor.com/Files/Download/setup_86b_multi.rar](http://www.china-victor.com/Files/Download/setup_86b_multi.rar)，方便将测试数据导出，计算结果
4. 将手机恢复出产设置，关闭数据连接(wifi,2G,3G)，手机屏幕设置常亮
5. 测试 SDK 未启动功耗
6. 测试 SDK 启动功耗
7. 计算差值则为 SDK 实际功耗


![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/sdk/android/res/images/battery-manager-1.png)


### 测试结果


在 SDK 默认参数下，Nexus 5 和 S4 的功耗表现如下表：

|         			| 前台静态功耗 	| 前台扫描功耗 | 后台静态功耗 	| 后台扫描功耗 |
| :--------------:	|:------------:	| :------:	| :------:| :------:|
| 三星 S4 （4.4.2）	| 181 mA       | 315 mA		| 174 mA  | 193 mA  |
| Nexus 5（5.1）		| 152 mA		| 268 mA     | 155 mA | 174 mA|

- 理论值</br>
	SDK 前台扫描为扫描 1100 ms，间隔 0ms，相当于前台不停扫描；后台扫描为扫描 5000 ms，间隔 30000 ms。以 35000 ms 为一个周期进行计算，理论上节省电量为 (35000 - 5000)/35000 = 85.71%。
	
- 实测值</br>
	在三星 S4 中，SDK 在前台的功耗为 134 mA，后台功耗为 19 mA，实际节省电量为 (134 - 19)/134 = 85.82%。</br>
	在 Nexus 5 中，SDK 在前台的功耗为 116 mA，后台功耗为 19 mA，实际节省电量为 (116 - 19)/116 = 83.62%。

根据测试结果分析，SDK 实际功耗与理论值是基本相同的。使用 SDK 默认设置，当 APP 进入后台模式之后，整体功耗会下降约 80%。开发者可以根据自己的需求，适当调整前台和后台的扫描参数。

<font color="red">
*说明:如果 SDK 开启后台唤醒模式，整体功耗在原有基础上将提高大约 20 mA。* 
</font>

### 结果验证

我们最后使用了示波器对手机实际功耗进行抓图，以验证测试结果的正确性。

由于后台模式默认周期较长，示波器波形表现不明显，很难估算结果，因此仅对前台模式的结果进行验证。

#### Nexus 5

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/sdk/android/res/images/battery-manager-2.png)

根据上述波形，以第二个波峰估算功耗：((500ms * 30mV/2 + (500ms * 10mV))/1000ms)/0.1Ω = 125 mA

计算结果与实测结果在误差为 9mA。

#### 三星 S4

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/sdk/android/res/images/battery-manager-3.png)

根据上述波形，功耗估算结果：(((200 * 60mV)/2 + (800 * 10mV))/1000ms)/0.1Ω = 140 mA


计算结果与实测结果在误差为 6mA。

## 省电模式的影响

需要注意的是，不能仅为了降低功耗，而无限制的延长扫描间隔，或者缩短扫描时间。

如果设置扫描间隔越长，那么发现 iBeacon 的时间也越长；如果扫描时间越短，那么就越不容发现 iBeacon。

因此我们建议的扫描时间不低于 iBeacon 广播时间间隔，目前大部分 iBeacon 的广播间隔是从 10 ms ~ 1000 ms 不等。

另外，影响 iBeacon 被发现的因素还与周围环境有关。周边的 iBeacon 越多，目标 iBeacon 相对越不容易被发现。

下面是根据周边 iBeacon 数量不同，发现目标 iBeacon 所需要的时间测试结果：

- iBeacon 参数：云子出厂默认配置（功率: -8 dbm,频率: 417.5 ms）
- 省电模式参数：SDK 默认配置（前台扫描时间：1100 ms，前台扫描间隔：0 ms，后台扫描时间：5000 ms，后台扫描间隔：30000 ms）
- 测试距离：1~2 m

<html>
	<body>
		<table border="1">
			<tr>
  				<th rowspan="2"></th>
  				<th colspan="5">前台扫描平均时间(ms)</th>
  				<th colspan="5">后台扫描平均时间(ms)</th>
			</tr>
			<tr>
  				<td>1</td>
  				<td>2</td>
  				<td>5</td>
  				<td>10</td>
  				<td>20</td>
  				<td>1</td>
  				<td>2</td>
  				<td>5</td>
  				<td>10</td>
  				<td>20</td>
			</tr>
			<tr>
 				<td>Nexus 5 (5.1)</td>
  				<td>1286</td>
  				<td>1306.5</td>
  				<td>2509.8</td>
  				<td>4071.2</td>
  				<td>4268.62</td>
  				<td>1996</td>
  				<td>1591.5</td>
  				<td>4121.75</td>
  				<td>31505.7</td>
  				<td>36519.5</td>
			</tr>
			<tr>
 				<td>三星 S4 (4.4.2)</td>
  				<td>1670.5</td>
  				<td>1867.33</td>
  				<td>2762.2</td>
  				<td>4113</td>
  				<td>4782.75</td>
  				<td>1867.33</td>
  				<td>1800.57</td>
  				<td>8722.74</td>
  				<td>37243.8</td>
  				<td>36702.5</td>
			</tr>
		</table>
	<body>
</html>

根据测试结果看出，前台扫描最多只需要 5 个扫描周期，就可以扫描到 20 个 iBeacon；后台扫描最多只需要 2 个周期，就可以扫描到 20 个 iBeacon。




