# Karoo Power Extension

> [!WARNING]  
> This app is currently in beta stage and its main features might not work at all.


This extension for Karoo devices adds a device simulates a virtual power meter. You only need to add this power meter (settings -> sensors) and you can use all power fields.

Compatible with Karoo 2 and Karoo 3 devices running Karoo OS version 1.524.2003 and later.

## Installation

You can sideload the app using the following steps for Karoo 2

1. Download the APK from the releases .
2. Prepare your Karoo for sideloading by following the [step-by-step guide](https://www.dcrainmaker.com/2021/02/how-to-sideload-android-apps-on-your-hammerhead-karoo-1-karoo-2.html) by DC Rainmaker.
3. Install the app using the command `adb install app-release.apk`.


If you've Karoo 3 and v > 1.527 you can sideload the app using the following steps:

1. Push link with apk (releases link) from your mobile.
2. Share with Hammerhead companion app
3. Install the app using the Hammerhead companion app.

## Usage

After installing this app on your Karoo, you need to configure the power extension in the settings. Please read the Help tab in configuration, there are some useful information.
This release has the following new features:
- Updated power estimation formula.
- Added wind speed parameter with openmeteo (from Timklge repository headwind).
- Added wind speed using openweathermap, you need to get an API key from openweathermap and introduce it in the configuration.
Openweathermap is a free service, but you need to create an account and get an API key. This service is more accurate than openmeteo, because it uses a lot of weather stations to get the wind speed.

## Known issues

- Power meter is not 100% accurate, it is only a estimation based in power formula. It is not possible to get the real power data from the Karoo without a power meter.
There is currently a big important parameter that is not considered in the power estimation, the wind. The wind can change the power needed to maintain a speed. 
I am working to add this parameter in the power estimation, but you can introduce the headwind speed manually in the configuration.
A not very good estimation for this is take the value from this https://headwind.app/ or use 0.0 as default value.

- Tested only with Karoo 3 and Metric configuration.

## Credits

- Made possible by the generous usage terms of timklge. He has a great development and I use part of his code to create this extension.
  https://github.com/timklge?tab=repositories
- Power estimation https://www.gribble.org/cycling/power_v_speed.html

## Links

[karoo-ext source](https://github.com/hammerheadnav/karoo-ext)
