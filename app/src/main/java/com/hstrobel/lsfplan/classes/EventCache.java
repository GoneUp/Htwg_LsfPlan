package com.hstrobel.lsfplan.classes;

import android.util.Log;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * On request +/-3 days should be calculated and saved.
 */
public class EventCache {
    private static final String TAG = "LSF";
    private static final int DAYS_TO_CACHE = 3;

    private Map<java.util.Calendar, List<VEvent>> cache;
    private Calendar globalCal;

    public EventCache() {
        cache = new TreeMap<>();
    }


    public List<VEvent> getDay(java.util.Calendar day) {
        Log.i(TAG, "getDay: Query for " + day.get(java.util.Calendar.DAY_OF_MONTH));
        if (Globals.myCal == null)
            throw new IllegalArgumentException("myCal is null!");
        if (globalCal != Globals.myCal) {
            Log.i(TAG, "getDay: Cleared Cache");
            cache.clear();
            globalCal = Globals.myCal;
        }

        day.clear(java.util.Calendar.HOUR);
        day.clear(java.util.Calendar.HOUR_OF_DAY);
        day.clear(java.util.Calendar.MINUTE);
        day.clear(java.util.Calendar.SECOND);
        day.clear(java.util.Calendar.MILLISECOND);

        if (!cache.containsKey(day)) {
            //generateFullCache(day);
            generateDay(day);
        }

        //Send a Intent to background service to pregenerate
        Globals.SyncStart(Globals.mainActivity);
        return cache.get(day);
    }

    public void generateFullCache(java.util.Calendar day) {
        for (int i = -DAYS_TO_CACHE; i < DAYS_TO_CACHE; i++) {
            java.util.Calendar actualDay = (java.util.Calendar) day.clone();
            actualDay.add(java.util.Calendar.DATE, i);

            if (!cache.containsKey(actualDay)) {
                generateDay(actualDay);
            }
        }
    }


    private void generateDay(java.util.Calendar day) {
        List<VEvent> evs = CalenderUtils.getEventsForDay(Globals.myCal, day);
        CalenderUtils.sortEvents(evs);

        cache.put(day, evs);
    }


}
