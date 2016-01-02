# Stolpersteine Android App

[![Build Status](https://travis-ci.org/Stolpersteine/stolpersteine-android.svg?branch=master)](https://travis-ci.org/Stolpersteine/stolpersteine-android)

Andoid app to display the locations of [Stolpersteine](http://en.wikipedia.org/wiki/Stolperstein) on a map. 

[![Get it on Google Play](https://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.option_u.stolpersteine)

## Contributors

- [Jing Li](https://github.com/thyrlian) ([@thyrlian](https://twitter.com/thyrlian))
- [Claus Höfele](http://github.com/choefele) ([@claushoefele](https://twitter.com/claushoefele))
- Hendrik Spree ([@drikkes](https://twitter.com/drikkes))
- Rachel Höfele
- Tom Reinert ([@tomreinert](https://twitter.com/tomreinert))
- [Peter Jeschke](https://github.com/looperhacks) ([@looperhacks](https://github.com/looperhacks))

## Building the App with Android Studio

1. Make sure you have [Java SE 7](http://www.oracle.com/technetwork/articles/javase/index-jsp-138363.html) or higher installed
2. Install [Android Studio](https://developer.android.com/sdk/installing/studio.html)
3. From within Android Studio's SDK Manager, install Android SDK (API 20) and SDK Tools (20)
4. Import the project from the folder where you checked out this repo
5. Use your own API key in AndroidManifest.xml or have the SHA1 of your keystore registered with an existing API key (`keytool -list -v -keystore ~/.android/debug.keystore`)
6. Configure a virtual device with Android 4.x with Google Play Services ([Guide to install Google Play Services on Genymotion](http://stackoverflow.com/questions/17831990/how-do-you-install-google-frameworks-play-accounts-etc-on-a-genymotion-virtu). Alternatively, use a hardware device

To run the app, configure an Android Application in Run/Debug Configurations with the Stolpersteine Module. In a similar way, you can create an Android Tests configuration to run the unit tests. Build variants for different cities are chosen using the Build Variants window.

### Android Version

API Level 15+, Android 4.0.x (Ice Cream Sandwich) or higher

## License (MIT)

Copyright (C) 2013 Option-U Software

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
