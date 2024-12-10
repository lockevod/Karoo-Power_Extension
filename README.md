# Karoo Power Extension

> [!WARNING]  
> This app is currently in beta stage and its main features might not work at all. 

This extension for Karoo devices adds a device simulates a virtual power meter. You only need to add this power meter (settings -> sensors) and you can use all power fields.

Compatible with Karoo 2 and Karoo 3 devices running Karoo OS version 1.524.2003 and later.

## Installation

You can sideload the app.

1. Download the APK from the releases .
2. Prepare your Karoo for sideloading by following the [step-by-step guide](https://www.dcrainmaker.com/2021/02/how-to-sideload-android-apps-on-your-hammerhead-karoo-1-karoo-2.html) by DC Rainmaker.
3. Install the app using the command `adb install app-release.apk`.

## Usage

After installing this app on your Karoo, you need to configure the power extension in the settings. Please read the Help tab in configuration, there are some useful information.

## Known issues

Power meter is not 100% accurate, it is only a estimation based in power formula. It is not possible to get the real power data from the Karoo without a power meter.
There is currently a big important parameter that is not considered in the power estimation, the wind. The wind can change the power needed to maintain a speed. 
I am working to add this parameter in the power estimation, but you can introduce the headwind speed manually in the configuration.
A not very good estimation for this is take the value from this https://headwind.app/ or use 0.0 as default value.


## Credits

- Made possible by the generous usage terms of timklge. He has a great development and I use part of his code to create this extension.
  https://github.com/timklge?tab=repositories
- Power estimation https://www.gribble.org/cycling/power_v_speed.html

## Links

[karoo-ext source](https://github.com/hammerheadnav/karoo-ext)