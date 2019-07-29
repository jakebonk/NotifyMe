[![](https://jitpack.io/v/jakebonk/NotifyMe.svg)](https://jitpack.io/#jakebonk/NotifyMe)

# NotifyMe
A Android Library for simple notifications. Very easily set a delay or time when you want the notification to popup. Notification will popup through system reboots.

![Demo](https://thumbs.gfycat.com/DishonestPlushBlacklab-size_restricted.gif)

## Download library with Jitpack.io
Add this to your build.gradle file for your app.
```java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```	

Add this to your dependencies in build.gradle for your project.
```java
	dependencies {
	        implementation 'com.github.jakebonk:NotifyMe:1.0.1'
	}
```

## Example

Create a NotifyMe Builder Object

```java

NotifyMe.Builder notifyMe = new NotifyMe.Builder(getApplicationContext());

```

Then set the fields you want.

```java
  
  notifyMe.title(String title);
  notifyMe.content(String content);
  notifyMe.color(Int red,Int green,Int blue,Int alpha);//Color of notification header
  notifyMe.led_color(Int red,Int green,Int blue,Int alpha);//Color of LED when notification pops up
  notifyMe.time(Calendar time);//The time to popup notification
  notifyMe.delay(Int delay);//Delay in ms
  notifyMe.large_icon(Int resource);//Icon resource by ID
  notifyMe.rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")//RRULE for frequency of notification
  notifyMe.addAction(Intent intent,String text); //The action will call the intent when pressed
  
```

After all the fields that you want are set just call build()!

```java

  notifyMe.build();

```
