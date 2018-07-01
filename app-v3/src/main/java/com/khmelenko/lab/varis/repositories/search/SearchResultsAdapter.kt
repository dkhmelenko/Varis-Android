package com.khmelenko.lab.varis.repositories.search

import android.content.Context
import android.database.Cursor
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.khmelenko.lab.varis.R
import com.khmelenko.lab.varis.storage.SearchHistoryProvider

/**
 * Adapter for search results
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
class SearchResultsAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, false) {

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val text = view.findViewById<TextView>(R.id.item_search_result_text)
        val columnIndex = cursor.getColumnIndex(SearchHistoryProvider.HEADER_COLUMN)
        val textValue = cursor.getString(columnIndex)
        text.text = textValue
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflater.inflate(R.layout.item_search_result, parent, false)
    }
}
