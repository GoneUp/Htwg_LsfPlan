package com.hstrobel.lsfplan.tests;

import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.model.calender.EventCache;

import junit.framework.Assert;

import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.Mockito.when;


/**
 * Created by Henry on 30.05.2016.
 */


@RunWith(RobolectricTestRunner.class) //
@Config(manifest = Config.NONE)
public class EventCacheTest {

    @Mock
    GlobalState globalState = GlobalState.getInstance();

    @Test
    public void testGetDay() throws Exception {
        when(globalState.myCal).thenReturn(new net.fortuna.ical4j.model.Calendar());

        Calendar cal = GregorianCalendar.getInstance();
        EventCache cache = new EventCache(false);

        List<VEvent> list = cache.getDay(cal);
        Assert.assertEquals(0, list.size());
    }
}