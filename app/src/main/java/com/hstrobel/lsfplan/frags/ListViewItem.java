package com.hstrobel.lsfplan.frags;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    public final MainListFragment fragment;
    public final Drawable icon;       // the drawable for the ListView item ImageView
    public final Drawable icon2;       // the drawable for the ListView item ImageView
    public final String title;        // the text for the ListView item title
    public final String description;  // the text for the ListView item description

    public ListViewItem(Drawable icon, Drawable icon2, String title, String description, MainListFragment fragment) {
        this.icon2 = icon2;
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.fragment = fragment;
    }
}