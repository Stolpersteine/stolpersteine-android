package com.option_u.stolpersteine.activities.description;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by Jing Li on 27/04/16.
 */
public class StolpersteineWebView extends WebView {
    public StolpersteineWebView(Context context) {
        super(context);
        config();
    }

    public StolpersteineWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        config();
    }

    public StolpersteineWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        config();
    }

    private void config() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
    }
}
