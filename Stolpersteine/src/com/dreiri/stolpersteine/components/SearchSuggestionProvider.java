package com.dreiri.stolpersteine.components;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dreiri.stolpersteine.api.StolpersteinNetworkService;
public class SearchSuggestionProvider extends ContentProvider {
    
    private static final String AUTHORITY = "com.dreiri.stolpersteine.suggestions";
    private static final String BASE_PATH = "search";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    
    public static final int STOLPERSTEINE = 110;
    public static final int STOLPERSTEIN_ID = 100;
//    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/stolpersteine";
//    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/stolpersteine";
    
    private static final int SEARCH_SUGGEST = 1;
    private static final String[] SEARCH_SUGGEST_COLUMNS = {
        BaseColumns._ID,
        SearchManager.SUGGEST_COLUMN_TEXT_1,
        SearchManager.SUGGEST_COLUMN_TEXT_2
//        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
    };
    private static final UriMatcher URI_MATCHER;
    
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
//        URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
//        URI_MATCHER.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, STOLPERSTEINE);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, STOLPERSTEIN_ID);
    }
    
    private StolpersteinNetworkService networkService;

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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
    	String keyword = selectionArgs[0];
        MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 1);
        Object[] row0 = new Object[] {1, keyword, "test2"};
        cursor.addRow(row0);
    	
        return cursor;
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
