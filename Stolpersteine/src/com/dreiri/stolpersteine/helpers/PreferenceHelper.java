package com.dreiri.stolpersteine.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.dreiri.stolpersteine.activities.bio.BioActivity.ViewFormat;

public class PreferenceHelper {

    private SharedPreferences prefs;
    private Editor editor;

    public PreferenceHelper(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void modifyValues(Callback callback) {
        this.editor = prefs.edit();
        callback.execute();
        editor.apply();
    }

    private interface Callback {
        void execute();
    }

    public void saveViewFormat(final ViewFormat viewFormat) {
        modifyValues(new Callback() {
            @Override
            public void execute() {
                editor.putString("ViewFormat", viewFormat.toString());
            }
        });
    }

    public ViewFormat readViewFormat() {
        String viewFormatString = prefs.getString("ViewFormat", ViewFormat.TEXT.toString());
        return ViewFormat.toViewFormat(viewFormatString);
    }

}
