package com.z4mod.z4root2;

import android.app.Application;

public class BaseApplication extends Application {

	 @Override
	    public void onCreate() {
	        super.onCreate();
	        DebugLog.registerUncaughtExceptionHandler(this);
	    }
}
