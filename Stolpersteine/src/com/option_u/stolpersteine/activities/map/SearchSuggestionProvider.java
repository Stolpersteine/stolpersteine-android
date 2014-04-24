package com.option_u.stolpersteine.activities.map;

import java.util.List;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;

import com.option_u.stolpersteine.api.SearchData;
import com.option_u.stolpersteine.api.StolpersteinNetworkService;
import com.option_u.stolpersteine.api.RetrieveStolpersteineRequest.Callback;
import com.option_u.stolpersteine.api.model.Person;
import com.option_u.stolpersteine.api.model.Stolperstein;

public class SearchSuggestionProvider extends ContentProvider {
    private static final long REQUEST_DELAY_MS = 300;
    
    public static final String AUTHORITY = "com.option_u.stolpersteine.suggestions";
    private static final String BASE_PATH = "search";
    
    private static final int STOLPERSTEINE = 110;
    private static final int STOLPERSTEIN_ID = 100;
    private static final int LIST_SIZE = 10;
    private static final int SEARCH_SUGGEST = 1;
    public static final String SUGGEST_COLUMN_URL = "url";
    private static final String[] SEARCH_SUGGEST_COLUMNS = {
        BaseColumns._ID,
        SearchManager.SUGGEST_COLUMN_TEXT_1,
        SearchManager.SUGGEST_COLUMN_TEXT_2,
        SUGGEST_COLUMN_URL
    };
    private static final UriMatcher URI_MATCHER;
    
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, STOLPERSTEINE);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, STOLPERSTEIN_ID);
    }
    
    private StolpersteinNetworkService networkService;
    private Object lastRequestTag;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }
    
    public void setNetworkService(StolpersteinNetworkService networkService) {
    	this.networkService = networkService;
    }
    
    @Override
    public Cursor query(final Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // New empty cursor that can be notified
    	final MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
    	final ContentResolver contentResolver = getContext().getContentResolver();
        cursor.setNotificationUri(contentResolver, uri);
        
        // Fire network request with a delay so it can be canceled when user types quickly
        final String keyword = selectionArgs[0];
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              request(cursor, contentResolver, uri, keyword);
          }
        }, REQUEST_DELAY_MS);
    	
        return cursor;
    }
    
    private void request(final MatrixCursor cursor, final ContentResolver contentResolver, final Uri uri, final String keyword) {
        // Cancel previous request
        networkService.cancelRequest(lastRequestTag);

        // Request new data
        SearchData searchData = new SearchData();
        searchData.setKeyword(keyword);
        lastRequestTag = networkService.retrieveStolpersteine(searchData, 0, LIST_SIZE, new Callback() {
            @Override
            public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
                if (stolpersteine != null) {
                    for (int i = 0; i < stolpersteine.size(); i++) {
                        Stolperstein stolperstein = stolpersteine.get(i);
                        Person person = stolperstein.getPerson();
                        String name = person.getNameAsString();
                        String street = stolperstein.getLocation().getStreet();
                        String url = person.getBiographyUri().toString();;
                        cursor.addRow(new Object[] {i, name, street, url});
                    }
                    contentResolver.notifyChange(uri, null);
                }
            }
        });
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

}
