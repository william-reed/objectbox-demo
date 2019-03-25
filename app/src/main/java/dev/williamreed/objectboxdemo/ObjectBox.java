package dev.williamreed.objectboxdemo;

import android.content.Context;

import io.objectbox.BoxStore;

/**
 * Singleton access for ObjectBox
 */
public class ObjectBox {
    private static BoxStore boxStore;

    static void init(Context context) {
        boxStore = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    public static BoxStore get() {
        return boxStore;
    }
}

