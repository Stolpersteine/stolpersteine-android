package com.option_u.stolpersteine.activities.description;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

class SimpleWebViewClient extends WebViewClient {

    private ProgressBar progressBar;

    public SimpleWebViewClient(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

}
