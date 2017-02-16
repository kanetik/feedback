Rverbio Android SDK
-------------------

The Rverbio Android SDK enables you to get feedback from your customers with minimal effort on your part. You can be up and running with just 2 lines of code:

    Rverbio.initialize(this);
    Rverbio.getInstance().showDialog(this);

**Features**

Rverbio offers some important customization features, with more to come. If you have requests for other features, never hesitate to contact us at <support@rverb.io>.

* Enable/Disable screenshot by default  
* Add custom data to all feedback

**Installation**

In your module's gradle.config, add the following line to your dependencies:
    
    compile 'io.rverb:feedback:1.0.0'
   
**Quick Start**

In your custom Application's onCreate method, add this line:

    Rverbio.initialize(this);
    
You are now ready to capture feedback! When you want to pop the feedback dialog from an activity, simply invoke like so:

    Rverbio.getInstance().showDialog(this);
    
And we'll take care of the rest!

**Advanced Setup**

* Will have stuff about initializing w/ Auto-screenshot disabled.
* Will also have stuff about calling Rverbio.initialize(this) from an activity, if you don't have a custom Application.
    
**Custom Data**

* Will have stuff about custom Key/Value pairs

**Support**

If you are having issues with the SDK, please let us know. We have a mailing list located at: <support@rverb.io>
We also have an Issue Tracker at <https://github.com/rverbio/android-library/issues>

**License**

The SDK is licensed under the Apache 2.0 license.
