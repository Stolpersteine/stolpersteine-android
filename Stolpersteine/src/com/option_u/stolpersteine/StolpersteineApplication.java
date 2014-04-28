package com.option_u.stolpersteine;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;

import android.app.Application;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.option_u.stolpersteine.activities.cards.CardsActivity;
import com.option_u.stolpersteine.activities.description.DescriptionActivity;
import com.option_u.stolpersteine.activities.map.MapActivity;

public class StolpersteineApplication extends Application {
    private Tracker tracker;
    private HashMap<String, String> classToViewNameMapping;
    
    @Override
    public void onCreate() {
//        GoogleAnalytics.getInstance(this).getLogger().setLogLevel(LogLevel.VERBOSE);
//        GoogleAnalytics.getInstance(this).setDryRun(true);
        
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(30);
        tracker = GoogleAnalytics.getInstance(this).newTracker("UA-38166041-3");
        tracker.setAnonymizeIp(false);
        
        UncaughtExceptionHandler handler = new ExceptionReporter(tracker, Thread.getDefaultUncaughtExceptionHandler(), this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
        
        classToViewNameMapping = new HashMap<String, String>();
        classToViewNameMapping.put(MapActivity.class.getName(), "Map");
        classToViewNameMapping.put(CardsActivity.class.getName(), "Cards");
        classToViewNameMapping.put(DescriptionActivity.class.getName(), "Description");
        
        super.onCreate();
    }
    
    public <T> String getString(Class<T> clazz) {
        String className = clazz.getName();
        String string = classToViewNameMapping.get(className);
        assert(string != null);
        
        return string;
    }

    public <T> void trackView(Class<T> clazz) {
        String screenName = getString(clazz);
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
        tracker.setScreenName(null);
    }
}
