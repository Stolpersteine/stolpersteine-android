package com.option_u.stolpersteine.activities.description;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.joanzapata.pdfview.PDFView;
import com.option_u.stolpersteine.R;
import com.option_u.stolpersteine.StolpersteineApplication;
import com.option_u.stolpersteine.helpers.PreferenceHelper;

public class DescriptionActivity extends Activity {

    private static final String EXTRA_NAME = "url";

    public enum ViewFormat {
        TEXT, WEB;

        public static ViewFormat toViewFormat(String viewFormatString) {
            try {
                return valueOf(viewFormatString);
            } catch (Exception e) {
                return TEXT;
            }
        }
    };

    private ViewFormat viewFormat;
    private PreferenceHelper preferenceHelper;
    private WebView browser;
    private PDFView pdfView;
    private long downloadReference;
    private String bioUrl;
    private static final String CSS_QUERY_STOLPERSTEINE_BERLIN = "div#biografie_seite";
    private static final String PREFIX_GERMAN = "http://www.stolpersteine-berlin.de/de";
    private static final String PREFIX_ENGLISH = "http://www.stolpersteine-berlin.de/en";
    private static final String PDF_VIEWER = "http://docs.google.com/viewer?embedded=true&url=";

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

                DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(referenceId);
                Cursor cursor = downloadManager.query(query);

                // it shouldn't be empty, but just in case
                if (!cursor.moveToFirst()) {
                    Log.e("Stolpersteine", "Empty row");
                    return;
                }

                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL != cursor.getInt(statusIndex)) {
                    Log.w("Stolpersteine", "Download Failed");
                    return;
                }

                int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String downloadedPackageUriString = cursor.getString(uriIndex);
                File file = new File(getFilePathFromUri(DescriptionActivity.this, Uri.parse(downloadedPackageUriString)));
                PDFView pdfView = (PDFView)findViewById(R.id.pdfview);
                pdfView.fromFile(file)
                       .defaultPage(1)
                       .load();
            }
        }
    };

    public static Intent createIntent(Context context, String url) {
        // Use English web site for Berlin biographies if not using German
        Locale locale = context.getResources().getConfiguration().locale;
        if (!locale.getLanguage().equals(Locale.GERMAN.getLanguage()) && url.startsWith(PREFIX_GERMAN)) {
            url = url.replace(PREFIX_GERMAN, PREFIX_ENGLISH);
        }

        Intent intent = new Intent(context, DescriptionActivity.class);
        intent.putExtra(EXTRA_NAME, url);

        return intent;
    }

    private static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = { MediaStore.MediaColumns.DATA };
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bioUrl = intent.getStringExtra(EXTRA_NAME);

        if (bioUrl.endsWith(".pdf")) {
            setContentView(R.layout.activity_description_pdf);

            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);

            Uri Download_Uri = Uri.parse(bioUrl);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
            DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
            downloadReference = downloadManager.enqueue(request);

//            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
//            progressBar.setVisibility(View.GONE);
//            pdfView = (PDFView)findViewById(R.id.pdfview);
//            pdfView.fromAsset("038_040_Lewkonja.pdf")
//                   .defaultPage(1)
//                   .load();
        } else {
            setContentView(R.layout.activity_description_web);

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
            browser = (WebView)findViewById(R.id.webview);
            browser.setWebViewClient(new SimpleWebViewClient(progressBar));
            WebSettings settings = browser.getSettings();
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);

            preferenceHelper = new PreferenceHelper(this);
            viewFormat = preferenceHelper.readViewFormat();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        StolpersteineApplication stolpersteineApplication = (StolpersteineApplication) getApplication();
        stolpersteineApplication.trackView(this.getClass());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bio, menu);
        MenuItem itemViewFormat = menu.getItem(0);
        openUrlBasedOnDomain(itemViewFormat);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_view_format) {
            if (viewFormat == ViewFormat.TEXT) {
                switchToAndLoadInWebView(item);
            } else {
                switchToAndLoadInTextView(item);
            }
        } else if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void loadContentInBrowser(WebView browser, String url, String cssQuery) {
        new HTMLContentLoader(browser).loadContent(this, url, cssQuery);
    }

    protected void loadUrlInBrowser(WebView browser, String url) {
        browser.loadUrl(url);
    }

    private void switchToAndLoadInTextView(MenuItem selectedItem) {
        viewFormat = ViewFormat.TEXT;
        preferenceHelper.saveViewFormat(viewFormat);
        setViewFormatMenuItemToWeb(selectedItem);

        WebSettings settings = browser.getSettings();
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(false);
        loadContentInBrowser(browser, bioUrl, CSS_QUERY_STOLPERSTEINE_BERLIN);
    }

    private void switchToAndLoadInWebView(MenuItem selectedItem) {
        viewFormat = ViewFormat.WEB;
        preferenceHelper.saveViewFormat(viewFormat);
        setViewFormatMenuItemToText(selectedItem);

        WebSettings settings = browser.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        loadUrlInBrowser(browser, bioUrl);
    }

    private void setViewFormatMenuItemToText(MenuItem item) {
        item.setTitle(R.string.bio_action_item_text);
        item.setIcon(R.drawable.ic_action_view_as_text);
    }

    private void setViewFormatMenuItemToWeb(MenuItem item) {
        item.setTitle(R.string.bio_action_item_web);
        item.setIcon(R.drawable.ic_action_view_as_web);
    }

    private void openUrlBasedOnDomain(MenuItem itemViewFormat) {
        if (bioUrl.contains("stolpersteine-berlin")) {
            // Load in whatever view provided by ViewFormat
            loadViewBasedOnViewFormat(itemViewFormat);
        } else if (bioUrl.endsWith(".pdf")) {

        } else {
            // Load in web only, and disable item option for unknown domain sources
            // e.g.: wikipedia.org
            loadUrlInBrowser(browser, bioUrl);
            disableMenuItem(itemViewFormat);
        }
    }

    private void loadViewBasedOnViewFormat(MenuItem itemViewFormat) {
        if (viewFormat == ViewFormat.WEB) {
            switchToAndLoadInWebView(itemViewFormat);
        } else {
            switchToAndLoadInTextView(itemViewFormat);
        }
    }

    private void disableMenuItem(MenuItem menuItem) {
        menuItem.setEnabled(false);
        menuItem.setVisible(false);
    }

}
