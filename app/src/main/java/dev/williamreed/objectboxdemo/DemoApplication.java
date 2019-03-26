package dev.williamreed.objectboxdemo;

import android.app.Application;
import android.util.Log;

import dev.williamreed.objectboxdemo.storage.ObjectBox;
import io.objectbox.android.AndroidObjectBrowser;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);

        boolean started = new AndroidObjectBrowser(ObjectBox.get()).start(this);
        Log.i("ObjectBrowser", "Started: " + started);
    }
}
