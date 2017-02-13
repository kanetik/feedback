Rverbio Android SDK
-------------------

The Rverbio Android SDK enables you to get feedback from your customers with little effort on your part. 

Look how easy it is to use:

    Rverbio.initialize(this);
    Rverbio.getInstance().showDialog(this);

**Installation**

In your module's gradle.config, add the following line to your dependencies:
    
    compile 'io.rverb:feedback:1.0.0'
   
An example of how the dependencies section might look is below:

    dependencies {
        compile fileTree(include: ['*.jar'], dir: 'libs')
        compile 'com.android.support:appcompat-v7:25.1.1'
        compile 'com.android.support:design:25.1.1'
        compile 'io.rverb:feedback:1.0.0'
    }

**Features**

Enable/Disable screenshot by default
Add custom data to all feedback

**Support**

If you are having issues with the SDK, please let us know. We have a mailing list located at: support@rverb.io
Issue Tracker: github.com/rverbio/android-library/issues

**License**

The project is licensed under the Apache 2.0 license.
