package com.khmelenko.lab.varis.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khmelenko.lab.varis.R;
import com.khmelenko.lab.varis.storage.SearchHistoryProvider;

/**
 * Adapter for search results
 *
 * @author Dmytro Khmelenko (d.khmelenko@gmail.com)
 */
public class SearchResultsAdapter extends CursorAdapter {

    public SearchResultsAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text = (TextView) view.findViewById(R.id.item_search_result_text);
        int columnIndex = cursor.getColumnIndex(SearchHistoryProvider.HEADER_COLUMN);
        String textValue = cursor.getString(columnIndex);
        text.setText(textValue);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.item_search_result, parent, false);
    }
}