package com.khmelenko.lab.varis.model;

/**
 * Defines the item in Navigation Drawer
 *
 * @author Dmytro Khmelenko
 */
public final class DrawerItem {

    private final int mIcon;
    private final String mTitle;

    public DrawerItem(int icon, String text) {
        mIcon = icon;
        mTitle = text;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }
}
