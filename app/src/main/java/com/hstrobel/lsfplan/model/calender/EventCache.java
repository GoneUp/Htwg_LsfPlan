package com.hstrobel.lsfplan.model.calender;

import android.os.AsyncTask;
import android.util.Log;

import com.hstrobel.lsfplan.GlobalState;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * On request +/-3 days should be calculated and saved.
 */
public class EventCache {
    private static final String TAG = "LSF";
    private static final int DAYS_TO_CACHE = 10;

    private Map<java.util.Calendar, List<VEvent>> cache;
    private Calendar globalCal;
    private boolean preGenerate;


    public EventCache(boolean preGenerate) {
        this.preGenerate = preGenerate;
        cache = new TreeMap<>();
    }

    public List<VEvent> getDay(java.util.Calendar day) {
        final java.util.Calendar localDay = GregorianCalendar.getInstance();
        localDay.setTime(day.getTime());

        Log.i(TAG, "getDay: Query for " + localDay.get(java.util.Calendar.DAY_OF_MONTH));
        Calendar myCal = GlobalState.getInstance().myCal;
        if (myCal == null)
            throw new IllegalArgumentException("myCal is null!");
        if (globalCal != myCal) {
            Log.i(TAG, "getDay: Cleared Cache");
            cache.clear();
            globalCal = myCal;
        }

        localDay.set(java.util.Calendar.HOUR_OF_DAY, 0);
        localDay.set(java.util.Calendar.MINUTE, 1);
        localDay.set(java.util.Calendar.SECOND, 0);
        localDay.set(java.util.Calendar.MILLISECOND, 0);

        if (!cache.containsKey(localDay)) {
            generateDay(localDay);
        }

        if (preGenerate) {
            //Generate the rest delayed
            BgAsyncTask bgTask = new BgAsyncTask();
            bgTask.execute(localDay);
        }
        return cache.get(localDay);
    }

    private void generateFullCache(java.util.Calendar day) {
        for (int i = -DAYS_TO_CACHE; i < DAYS_TO_CACHE; i++) {
            java.util.Calendar actualDay = (java.util.Calendar) day.clone();
            actualDay.add(java.util.Calendar.DAY_OF_MONTH, i);

            if (!cache.containsKey(actualDay)) {
                generateDay(actualDay);
            }
        }
    }

    private void generateDay(java.util.Calendar day) {
        try {
            List<VEvent> evs = CalenderUtils.getEventsForDay(GlobalState.getInstance().myCal, day);
            CalenderUtils.sortEvents(evs);

            cache.put(day, evs);
        } catch (Exception ex) {
            Log.e(TAG, "generateDay failed", ex);
        }
    }


    private class BgAsyncTask extends AsyncTask<java.util.Calendar, String, String> {
        @Override
        protected String doInBackground(java.util.Calendar... params) {
            if (params.length == 0 || params[0] == null)
                return null;
            generateFullCache(params[0]);
            return null;
        }
    }
}
