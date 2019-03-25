package dev.williamreed.objectboxdemo;

import android.app.Application;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
    }
}
