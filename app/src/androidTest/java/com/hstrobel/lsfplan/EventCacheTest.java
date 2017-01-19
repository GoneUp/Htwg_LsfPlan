package com.hstrobel.lsfplan;

import com.hstrobel.lsfplan.model.calender.EventCache;

import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Henry on 30.05.2016.
 */

public class EventCacheTest {

    @Test
    public void testGetDay() throws Exception {
        Calendar cal = GregorianCalendar.getInstance();
        EventCache cache = new EventCache();

        List<VEvent> list = cache.getDay(cal);
        Assert.assertEquals(0, list.size());
    }
}