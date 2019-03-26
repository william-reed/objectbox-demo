package dev.williamreed.objectboxdemo.storage;

import android.content.Context;

import dev.williamreed.objectboxdemo.models.MyObjectBox;
import io.objectbox.BoxStore;

/**
 * Singleton access for ObjectBox
 */
public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore get() {
        return boxStore;
    }
}
