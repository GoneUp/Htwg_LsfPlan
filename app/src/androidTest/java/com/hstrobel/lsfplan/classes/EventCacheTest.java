package com.hstrobel.lsfplan.classes;

import junit.framework.TestCase;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by Henry on 30.05.2016.
 */
public class EventCacheTest extends TestCase {

    @Test
    public void testGetDay() throws Exception {
        Calendar cal = new Calendar();
        EventCache cache = new EventCache(cal);


        List<VEvent> list = cache.getDay(java.util.Calendar.getInstance());
        assertEquals(0, list.size());
    }
}