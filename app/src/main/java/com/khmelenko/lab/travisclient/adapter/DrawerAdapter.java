package com.khmelenko.lab.travisclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.model.DrawerItem;

import java.util.List;

/**
 * Custom adapter for Navigation Drawer
 *
 * @author Dmytro Khmelenko
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    private List<DrawerItem> mItems;

    public DrawerAdapter(Context context, List<DrawerItem> items) {
        super(context, R.layout.item_drawer, items);
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_drawer, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.item_drawer_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.item_drawer_text);

        DrawerItem item = mItems.get(position);

        imgIcon.setImageResource(item.getIcon());
        txtTitle.setText(item.getTitle());

        return convertView;
    }

}
