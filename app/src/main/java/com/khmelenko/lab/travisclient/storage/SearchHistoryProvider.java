package com.khmelenko.lab.travisclient.storage;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.provider.SearchRecentSuggestions;

import java.util.ArrayList;
import java.util.List;

/**
 * Content provider for recent search history
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.khmelenko.lab.travisclient.storage.SearchHistoryProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    // column with the search text
    public static final String HEADER_COLUMN = "display1";

    public SearchHistoryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

    /**
     * Queries recent search data
     *
     * @param context Context
     * @param query   Query string
     * @return Cursor with found data or null
     */
    public static Cursor queryRecentSearch(Context context, String query) {
        Uri uri = Uri.parse("content://".concat(SearchHistoryProvider.AUTHORITY.concat("/suggestions")));

        String[] selection = SearchRecentSuggestions.QUERIES_PROJECTION_1LINE;
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = context.getContentResolver().query(
                uri,
                selection,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );

        return cursor;
    }

    /**
     * Transforms cursor to the list
     *
     * @param cursor Cursor
     * @return List with results
     */
    public static List<String> transformSearchResultToList(Cursor cursor) {
        List<String> resultList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Integer headerIdx = cursor.getColumnIndex(HEADER_COLUMN);
                String header = cursor.getString(headerIdx);
                resultList.add(header);
            }
        }

        return resultList;
    }
}
