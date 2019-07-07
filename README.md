# TapHoldUpButton

[![](https://jitpack.io/v/abdularis/TapHoldUpButton.svg)](https://jitpack.io/#abdularis/TapHoldUpButton)

Tap on it, Hold on it, it's easier to show you the preview.

# Preview
![](preview.gif)

# Usage
Add repository
~~~gradle
allprojects {
    repositories {
    ...
    maven {
        url 'https://jitpack.io' }
    }
}
~~~

Add dependency
~~~gradle
dependencies {
    implementation 'com.github.abdularis:TapHoldUpButton:0.1.1'
}
~~~


```xml
<com.aar.tapholdupbutton.TapHoldUpButton
        android:layout_width="76dp"
        android:layout_height="76dp"
        app:thub_ringStrokeWidth="2dp"
        app:thub_ringColor="#DDDDDD"
        app:thub_circleColor="#DDDDDD"
        app:thub_circleGap="3dp"
        app:thub_circleColorOhHold="#FA1A1A"/>
```
