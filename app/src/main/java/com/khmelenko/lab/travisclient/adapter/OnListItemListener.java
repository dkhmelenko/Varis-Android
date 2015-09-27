package com.khmelenko.lab.travisclient.adapter;

/**
 * Listener for the list items
 *
 * @author Dmytro Khmelenko
 */
public interface OnListItemListener {

    /**
     * Handles item selection
     *
     * @param position Item position
     */
    void onItemSelected(int position);
}
