Rverbio Android SDK
-------------------

The Rverbio Android SDK enables you to get feedback from your customers with as few as 2 lines of code.

**Features**
Rverbio offers some important customization features, with more to come. If you have requests for other features, never hesitate to contact us at <support@rverb.io>.

* Enable/Disable screenshot by default  
* Add custom data to feedback on the back-end
* Supply user identifiers such as an account ID so you can link a user across channels

**Installation**
In your module's gradle.config, add the following line to your dependencies:
    
    compile 'io.rverb:feedback:1.0.0'
   
**Quick Start**
In your custom Application's onCreate method, add this line:

    Rverbio.initialize(this);
    
You are now ready to capture feedback! When you want to pop the feedback dialog from an activity, simply invoke like so:

    Rverbio.getInstance().showDialog(this);
    
That's all you need to do -- we'll take care of the rest!

**Overriding the Default Settings**
You can initialize Rverbio with a RverbioOptions object, setting up defaults as you like. In version 1.0 of the SDK, the only default you can set is whether a screenshot is taken when the user initiates a feedback request:

    RverbioOptions options = new RverbioOptions();
    options.setAttachScreenshotEnabled(false);
    
    Rverbio.initialize(this, options);

**Initializing Without a Custom Application**
If you don't have a custom application, you can also initialize Rverbio from the first activity your app launches. There are a couple caveats, though:

* You will want to ensure that it is only initialized once. If it is initialized multiple times, everything will still work, but the "Session Started" logging will be inaccurate, and all data relating to session length and number of visits may be wrong.
* If a user can enter your app through different activities, you will need to ensure that Rverbio is initialized on any of the possible starting activities, but again, you will want to ensure that it is only initialized once.
    
**Custom Data**
At any time, after Rverbio has been initialized, you can pass data to the Rverbio instance, in the form of Key/Value Pairs, and those pieces of data will get attached to any feedback sent by the user.

For instance, if you tell Rverbio that a user has just purchased your game's latest power-up, if they submit feedback later that session, that data will get sent along with their feedback, and you will be able to see that when you view their request.

To add a Key/Value Pair, simply call this method on the Rverbio instance:

	Rverbio.getInstance().addContextDataItem("Last In-App Purchase", "Infinite Fuel Power-Up");

You can also add a Map, like so:

	Map<String, String> data = new ArrayMap<>();
    data.put("In App Purchase", "Infinite Fuel Power-Up");
    data.put("High Score", "123510351");
    
    Rverbio.getInstance().addContextDataItems(data);

**End User Data**
* Gotta fill out the docs with info about setting the user's email and/or identifier.

**Support**
If you are having issues with the SDK, please let us know.  
* We can always be reached at: <support@rverb.io>.  
* If you discover a bug, we have an Issue Tracker at <https://github.com/rverbio/android-library/issues>

**License**
The SDK is licensed under the Apache 2.0 license.