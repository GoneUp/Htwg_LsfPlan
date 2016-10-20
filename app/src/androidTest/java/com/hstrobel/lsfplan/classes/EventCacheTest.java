package com.hstrobel.lsfplan.classes;

import com.hstrobel.lsfplan.model.calender.EventCache;

import junit.framework.TestCase;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Henry on 30.05.2016.
 */
public class EventCacheTest extends TestCase {
    public void testGetDay() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        EventCache cache = new EventCache();

        List<VEvent> list = cache.getDay(cal);
        assertEquals(0, list.size());
    }
}