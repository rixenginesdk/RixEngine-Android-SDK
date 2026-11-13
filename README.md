# RixEngine Android SDK

English | [中文](README_ZH.md)

---

**Welcome to the RixEngine Android SDK**, your gateway to unlocking the full potential of in-app monetization.

This `RixEngine-Android-SDK` repository contains:

1. Example source code for using RixEngine
2. Open source mediation adapters

# Examples Demo App
The Java Demo App is sample projects demonstrating how to mediate ads using 
RixEngine SDK. To get started with the demo apps, follow the instructions below:

1. Open your desired project in Android Studio: RixEngine Demo App - Java.

2. Verify that the dependency 
```
    dependencies {
    
        //RixEngine Android SDK (Necessary)
    
        implementation 'io.github.rixenginesdk:rixengine:4.3.1'
    
    }
```

is included in your build.gradle (Module: app).

3. Change the package with your own unique identifier in your build.gradle (Module: app).
Base your unique identifier on the name of the application you will create or that you
have already created in the RixEngine dashboard.

4. Update the unique RixEngine ad unit ID value within the activity code for each ad format.
Each ad format corresponds to a unique RixEngine ad unit ID you create in the RixEngine 
dashboard for the package used before.

# Support

For technical support or inquiries, please contact [rix-sdk@rixengine.com](mailto:rix-sdk@rixengine.com).


