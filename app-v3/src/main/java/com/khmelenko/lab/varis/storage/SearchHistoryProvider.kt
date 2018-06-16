package com.khmelenko.lab.varis.storage

import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import android.provider.SearchRecentSuggestions

import com.khmelenko.lab.varis.BuildConfig

import java.util.ArrayList

/**
 * Content provider for recent search history
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class SearchHistoryProvider : SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = BuildConfig.APPLICATION_ID + ".storage.SearchHistoryProvider"
        const val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES

        // column with the search text
        @JvmField
        val HEADER_COLUMN = "display1"

        /**
         * Queries recent search data
         *
         * @param context Context
         * @param query   Query string
         * @return Cursor with found data or null
         */
        fun queryRecentSearch(context: Context, query: String): Cursor? {
            val uri = Uri.parse("content://" + (SearchHistoryProvider.AUTHORITY + "/suggestions"))

            val selection = SearchRecentSuggestions.QUERIES_PROJECTION_1LINE
            val selectionArgs = arrayOf("%$query%")

            return context.contentResolver.query(
                    uri,
                    selection,
                    "display1 LIKE ?",
                    selectionArgs,
                    "date DESC"
            )
        }

        /**
         * Transforms cursor to the list
         *
         * @param cursor Cursor
         * @return List with results
         */
        fun transformSearchResultToList(cursor: Cursor?): List<String> {
            val resultList = ArrayList<String>()

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val headerIdx = cursor.getColumnIndex(HEADER_COLUMN)
                    val header = cursor.getString(headerIdx)
                    resultList.add(header)
                }
            }

            return resultList
        }
    }
}
