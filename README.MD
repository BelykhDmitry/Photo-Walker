### About this Application

The idea was inspired by Komoot interview test challenge.
Now it is my playground with usage of modern technologies pushing by Google.
There are Jetpack Compose, ViewModel, Navigation, Room, Coroutines + Flow, Coil.
Tech Plans:
  * Hilt
  * Crashlytics
  * Maps (OSM or Google, haven't decided yet)
  * Adopt for different screen sizes

#### Restrictions

minAPI: 21

Device with GMS:
I used google services API for location observing because it provides built-in navigation algorithms 
which use several sensors for current location calculation. Also these algorithms optimize power usage.
Application needs improvements to support different services such as HMS.
Devices without GMS are also supported.


#### Some learnings made while developing

1. There is a new permission starting from API 33: POST_NOTIFICATIONS. It's important to have it for a foreground service.
2. How to work with a GMS location provider.
3. There are few ways of receiving location: geofencing, subscription on updates, explicit current location request.
   There are few location precision modes affecting power consumption.
4. Testing location is difficult with a standard emulator with GMS. I was only successful with testing
   GMS on one of the latest version of android (33). Older versions of android without GMS work properly with location.
   This topic needs deeper research.
5. Location services use GPS not often. It's more effective to use WiFi spots, communication towers and inertial sensors.
