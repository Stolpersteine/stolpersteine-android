package com.dreiri.stolpersteine.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class NotificationHelper {
    
    public static void alertAndGoToSettings(Context context, String alertTitle, String alertMessage, String alertPosBtn, String alertNegBtn, String settingsIntent) {
        final Context ctx = context;
        final String settings = settingsIntent;
        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        alert.setTitle(alertTitle);
        alert.setMessage(alertMessage);
        
        alert.setPositiveButton(alertPosBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(settings);
                ctx.startActivity(intent);
            }
        });
        
        alert.setNegativeButton(alertNegBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        alert.show();
    }
    
    public static void alertAndGoToSettings(Context context, int alertTitleResource, int alertMessageResource, int alertPosBtnResource, int alertNegBtnResource, String settingsIntent) {
        String alertTitle = context.getString(alertTitleResource);
        String alertMessage = context.getString(alertMessageResource);
        String alertPosBtn = context.getString(alertPosBtnResource);
        String alertNegBtn = context.getString(alertNegBtnResource);
        alertAndGoToSettings(context, alertTitle, alertMessage, alertPosBtn, alertNegBtn, settingsIntent);
    }
    
}
