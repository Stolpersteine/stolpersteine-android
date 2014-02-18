package com.dreiri.stolpersteine.activities.map;

import java.util.List;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dreiri.stolpersteine.api.RetrieveStolpersteine.Callback;
import com.dreiri.stolpersteine.api.SearchData;
import com.dreiri.stolpersteine.api.StolpersteinNetworkService;
import com.dreiri.stolpersteine.api.model.Stolperstein;

public class SearchSuggestionProvider extends ContentProvider {
    
    private static final String AUTHORITY = "com.dreiri.stolpersteine.suggestions";
    private static final String BASE_PATH = "search";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    
    public static final int STOLPERSTEINE = 110;
    public static final int STOLPERSTEIN_ID = 100;
    private static final int LIST_SIZE = 10;
    private static final int SEARCH_SUGGEST = 1;
    private static final String[] SEARCH_SUGGEST_COLUMNS = {
        BaseColumns._ID,
        SearchManager.SUGGEST_COLUMN_TEXT_1,
        SearchManager.SUGGEST_COLUMN_TEXT_2
    };
    private static final UriMatcher URI_MATCHER;
    
    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
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
    
    private void searchForKeyword(String keyword, Callback callback) {
    	SearchData searchData = new SearchData();
    	searchData.setKeyword(keyword);
        networkService.retrieveStolpersteine(searchData, 0, LIST_SIZE, callback);
    }

    @Override
    public Cursor query(final Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
    	String keyword = selectionArgs[0];
    	final MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
    	searchForKeyword(keyword, new Callback() {
    		@Override
    		public void onStolpersteineRetrieved(List<Stolperstein> stolpersteine) {
				for (int i = 0; i < stolpersteine.size(); i++) {
					Stolperstein stolperstein = stolpersteine.get(i);
					String name = stolperstein.getPerson().getNameAsString();
					String street = stolperstein.getLocation().getStreet();
					cursor.addRow(new Object[] {i, name, street});
				}
				getContext().getContentResolver().notifyChange(uri, null, false);
    		}
    	});
    	
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
