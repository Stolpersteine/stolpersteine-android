package com.option_u.stolpersteine.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.option_u.stolpersteine.activities.description.DescriptionActivity.ViewFormat;

public class PreferenceHelper {

    private SharedPreferences prefs;

    public PreferenceHelper(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void modifyValues(Callback callback) {
        Editor editor = prefs.edit();
        callback.execute(editor);
        editor.apply();
    }

    private interface Callback {
        void execute(Editor editor);
    }

    public void saveViewFormat(final ViewFormat viewFormat) {
        modifyValues(new Callback() {
            @Override
            public void execute(Editor editor) {
                editor.putString("ViewFormat", viewFormat.toString());
            }
        });
    }

    public ViewFormat readViewFormat() {
        String viewFormatString = prefs.getString("ViewFormat", ViewFormat.TEXT.toString());
        return ViewFormat.toViewFormat(viewFormatString);
    }

}
