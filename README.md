# NotifyMe
A Android Library for simple notifications. Very easily set a delay or time when you want the notification to popup. Notification will popup through system reboots.

##Example

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
  notifyMe.large_icon(Int resource);
  notifyMe.addAction(Intent intent,String text); //The action will call the intent when pressed
  
```

After all the fields that you want are set just call build()!

```java

  notifyMe.build();

```
