#云子示例应用介绍


“云子”示例应用，是一款帮助开发者体验云子传感器功能的手机客户端。目前支持iPhone版，你可以在我们的Github上找到对应的项目源码。

“云子”，基于SBK（Sensoro Beacon Kit）开发，需要在Xcode6、iOS8版本SDK下编译运行，支持iOS7.0及以上版本。


###主要功能：

####云子列表与搜索
显示周围云子的SN与ID，SN与ID和云子传感器对应的贴纸一致，可以通过搜索的方式找到指定的云子。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/1.PNG)

####系统权限验证  
因为云子是一款基于iBeacon的智能传感器，所以需要使用iBeacon对应所需的系统权限，包括定位服务，和系统蓝牙。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/0.PNG)

####云子功能与特点
“云子”的功能主要涵盖：
（1）iBeacon的RSSI值（信号强度），accuracy（距离），proximity（范围）。
（2）传感器：温度，光线，加速度传感器。
（3）利用云子可实现的功能及场景：距离、范围、光线、温度、移动、推送等。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/2.PNG)

####距离
当距云子的距离发生变化，页面会显示使用者距离云子的距离。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/3.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/4.PNG)

#####范围
当距云子的距离发生变化，使用者所处的位置可能会发生变化。每个云子定义了四种范围状态“很近”、“较近”、“较远”、“未知”。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/5.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/6.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/7.PNG)
#####光线
将云子的光线传感器开启，中心颜色会随着光线变化而变化。
	
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/8.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/9.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/10.PNG)
#####温度
将云子的温度传感器开启，云子芯片温度变化，页面背景会随着温度变化而变化。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/11.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/12.PNG)
#####移动
将云子的加速度传感器开启，当云子移动时，地球会转动；当云子停止移动时，计数器会加1。
	
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/13.PNG)
#####推送
当进入或者离开云子传感器时，依据传感器可将“云子”应用唤醒。“云子”应用会根据传感器的配置，判断是否需要生成一个推送提示。

![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/14.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/15.PNG)
![](https://raw.githubusercontent.com/Sensoro/Sensoro.github.io/master/download/app/yunzi/android/res/png/16.PNG)

License
=======

Copyright 2014 Sensoro, Inc.

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
