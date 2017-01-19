package com.hstrobel.lsfplan.tests;


import android.content.Context;

import com.hstrobel.lsfplan.BuildConfig;
import com.hstrobel.lsfplan.model.NotificationUtils;

import junit.framework.TestCase;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Henry on 15.01.2017.
 */


@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 23, constants = BuildConfig.class)
public class CalenderTests extends TestCase {
    private String testFile = "BEGIN:VCALENDAR\n" +
            "PRODID:QIS-LSF HIS GmbH\n" +
            "VERSION:2.0\n" +
            "BEGIN:VTIMEZONE\n" +
            "TZID:Europe/Berlin\n" +
            "X-LIC-LOCATION:Europe/Berlin\n" +
            "BEGIN:DAYLIGHT\n" +
            "TZOFFSETFROM:+0100\n" +
            "TZOFFSETTO:+0200\n" +
            "TZNAME:CEST\n" +
            "DTSTART:19700329T020000\n" +
            "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3\n" +
            "END:DAYLIGHT\n" +
            "BEGIN:STANDARD\n" +
            "TZOFFSETFROM:+0200\n" +
            "TZOFFSETTO:+0100\n" +
            "TZNAME:CET\n" +
            "DTSTART:19701025T030000\n" +
            "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10\n" +
            "END:STANDARD\n" +
            "END:VTIMEZONE\n" +
            "METHOD:PUBLISH\n" +
            "\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20161010T094500\n" +
            "DTEND;TZID=Europe/Berlin:20161010T111500\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20170127T235900Z;INTERVAL=1;BYDAY=MO\n" +
            "EXDATE:20161226T140000Z,\n" +
            "LOCATION:O - 107\n" +
            "DTSTAMP:20170115T181839Z\n" +
            "UID:155971278501\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:1453052420 - IT-Security\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "\n" +
            "END:VCALENDAR";


    @Test
    public void testHyphen() throws IOException, ParserException {
        Context appContext = RuntimeEnvironment.application.getApplicationContext();

        CalendarBuilder builder = new CalendarBuilder();
        Calendar myCal = builder.build(new StringReader(testFile));


        ComponentList components = myCal.getComponents(Component.VEVENT);
        for (Object obj : components) {
            if (obj instanceof VEvent) {
                VEvent event = (VEvent) obj;

                if (event.getSummary().getValue().equals("1453052420 - IT-Security")) {
                    String topicParsed = NotificationUtils.getTopic(event);
                    assertEquals("IT-Security", topicParsed);
                }
            }

        }

        assertNotNull(myCal);
    }

    @Test
    public void testLOL() {
        fail();
    }

}
