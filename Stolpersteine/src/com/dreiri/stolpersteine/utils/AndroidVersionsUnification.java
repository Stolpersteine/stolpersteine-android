package com.dreiri.stolpersteine.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;

public class AndroidVersionsUnification {
    private static int getCurrentApiLevel() {
        return android.os.Build.VERSION.SDK_INT;
    }
    
    /**
     * Uses different methods depends on Android API level
     * @param divideVersion is the API version which deprecates the old method
     * @param oldCall is called when < divideVersion
     * @param newCall is called when >= divideVersion
     */
    public static void doThisOrThat(int divideVersion, Callback oldCall, Callback newCall) {
        int currentApiLevel = getCurrentApiLevel();
        if (currentApiLevel < divideVersion) {
            oldCall.execute();
        } else {
            newCall.execute();
        }
    }
    
    @SuppressLint("NewApi")
    public static void setBackgroundForView(final View view, final Drawable background) {
        doThisOrThat(16, new Callback() {
            
            @SuppressWarnings("deprecation")
            @Override
            public void execute() {
                view.setBackgroundDrawable(background);
            }
        }, new Callback() {
            
            @Override
            public void execute() {
                view.setBackground(background);
            }
        });
    }
    
}
