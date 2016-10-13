package com.hstrobel.lsfplan.gui.eventlist;

import android.graphics.drawable.Drawable;

import net.fortuna.ical4j.model.component.VEvent;

public class EventItem {
    public final MainListFragment fragment;
    public final Drawable iconLeft;
    public final Drawable iconRight;
    public final String title;
    public final String description;
    public final VEvent sourceEvent;

    public EventItem(Drawable iconLeft, Drawable iconRight, String title, String description, MainListFragment fragment, VEvent event) {
        this.iconRight = iconRight;
        this.iconLeft = iconLeft;
        this.title = title;
        this.description = description;
        this.fragment = fragment;
        this.sourceEvent = event;
    }
}