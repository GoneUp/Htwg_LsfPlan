package com.hstrobel.lsfplan.tests;

import com.hstrobel.lsfplan.model.calender.EventCache;

import junit.framework.Assert;

import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * Created by Henry on 30.05.2016.
 */


@RunWith(RobolectricTestRunner.class) //
public class EventCacheTest {


    @Test
    public void testGetDay() throws Exception {


        Calendar cal = GregorianCalendar.getInstance();
        EventCache cache = new EventCache(false);

        List<VEvent> list = cache.getDay(cal);
        Assert.assertEquals(0, list.size());
    }
}