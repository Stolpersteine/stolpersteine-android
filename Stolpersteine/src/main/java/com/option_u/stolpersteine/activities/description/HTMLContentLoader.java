package com.option_u.stolpersteine.activities.description;

import android.app.Activity;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HTMLContentLoader {

    private WebView browser;

    public HTMLContentLoader(WebView browser) {
        this.browser = browser;
    }

    public void loadContent(final Activity activity, final String url, final String cssQuery) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.parse(new URL(url).openStream(), "utf-8", url);
                    Elements elements = document.select(cssQuery);
                    if (elements.isEmpty()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                browser.loadUrl(url);
                            }
                        });
                        return;
                    }
                    String content = elements.toString();
                    content = removeHTMLTagHyperlink(content);
                    DataLoaderRunnable dataLoaderRunnable = new DataLoaderRunnable(browser, content);
                    activity.runOnUiThread(dataLoaderRunnable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class DataLoaderRunnable implements Runnable {

        private WebView browser;
        private String data;
        private String mimeType = "text/html; charset=UTF-8";

        public DataLoaderRunnable(WebView browser, String data) {
            this.browser = browser;
            this.data = data;
        }

        @Override
        public void run() {
            browser.loadData(data, mimeType, null);
        }

    }

    private String removeHTMLTagHyperlink(String content) {
        Pattern pattern = Pattern.compile("<a\\s.+?>(.+?)</a>");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
