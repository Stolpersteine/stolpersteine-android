# Stolpersteine Android App

[![Build Status](https://api.travis-ci.org/optionu/stolpersteine-android.svg)](https://travis-ci.org/optionu/stolpersteine-android)

Andoid app to display the locations of [Stolpersteine](http://en.wikipedia.org/wiki/Stolperstein) on a map. 

[![Get it on Google Play](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.option_u.stolpersteine)

## Contributors

- [Jing Li](https://github.com/thyrlian) ([@thyrlian](https://twitter.com/thyrlian))
- [Claus Höfele](http://github.com/choefele) ([@claushoefele](https://twitter.com/claushoefele))

## Building the App with ADT

### Google Play Services

1. Install Google Play Services in Extras through the Android SDK Manager
2. File > Import > Android/Existing Android Code into Workspace and choose <android-sdk>/extras/google/google_play_services/libproject/google-play-services_lib/
3. Check "Is Library" in Properties > Android

See http://developer.android.com/google/play-services/setup.html

### Android Maps Utils

1. Make sure to also pull submodules (`git submodule init && git submodule update --recursive`)
2. File > Import > Android/Existing Android Code into Workspace and choose android-maps-utils/library
3. Check "Is Library" in Properties > Android
4. Add Google Play Services as dependency in Properties > Android
5. Add Stolpersteine/libs/android-support-v4.jar as dependency in Java Build Path > Libraries

See http://googlemaps.github.io/android-maps-utils/#start

### Run App

1. File > Import... > Android/Existing Android Code Into Workspace
2. Choose Stolpersteine folder and import
3. Add dependency to Google Play Services and Android Maps Utils in Properties > Android > Library
4. Fix the source-folders in Properties > Java Build Path > Source. You have to remove the /src-folder and add /src/main/java and /src/androidTest/java because Eclipse can't recognize standard layouts.
5. Use your own API key in AndroidManifest.xml or have the SHA1 of your keystore registered with an existing API key

    keytool -list -v -keystore ~/.android/debug.keystore
    
6. Configure a virtual device with Android 4.x with Google Play Services ([Guide to install Google Play Services on Genymotion](http://stackoverflow.com/questions/17831990/how-do-you-install-google-frameworks-play-accounts-etc-on-a-genymotion-virtu). Alternatively, use a hardware device
7. Run as Android Application

### Run Tests

1. File > Import... > Android/Existing Android Code Into Workspace
2. Choose StolpersteineTest folder and import
3. Add dependency to Stolpersteine in Properties > Java Build Path > Projects
3. Configure to run as Android JUnit Test

Note: set the text encoding in ADT to UTF-8 (Preferences > General > Workspace). Otherwise, test cases that use German characters will fail.

### Requires Android Version

API Level 15+, Android 4.0.x (Ice Cream Sandwich) or higher

## License (MIT)

Copyright (C) 2013 Option-U Software

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
