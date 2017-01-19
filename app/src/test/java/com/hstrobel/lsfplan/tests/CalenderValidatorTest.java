package com.hstrobel.lsfplan.tests;

import com.hstrobel.lsfplan.BuildConfig;
import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.model.Utils;
import com.hstrobel.lsfplan.model.calender.CalenderValidator;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Henry on 16.11.2015.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 23, constants = BuildConfig.class)
public class CalenderValidatorTest extends TestCase {
    String file = "BEGIN:VCALENDAR\n" +
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
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151012T080000\n" +
            "DTEND;TZID=Europe/Berlin:20151012T093000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=MO\n" +
            "EXDATE:20151123T140000Z,\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150577264859\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220110 - Mathematik 1 | AIN (Pleßke)\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151123T080000\n" +
            "DTEND;TZID=Europe/Berlin:20151123T093000\n" +
            "EXDATE:\n" +
            "LOCATION:G - 042\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150577270309\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220110 - Mathematik 1 | AIN\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151005T140000\n" +
            "DTEND;TZID=Europe/Berlin:20151005T153000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=MO\n" +
            "EXDATE:\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582269536\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151005T154500\n" +
            "DTEND;TZID=Europe/Berlin:20151005T171500\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=MO\n" +
            "EXDATE:\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582269537\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151110T154500\n" +
            "DTEND;TZID=Europe/Berlin:20151110T171500\n" +
            "EXDATE:\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582269546\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151117T154500\n" +
            "DTEND;TZID=Europe/Berlin:20151117T171500\n" +
            "EXDATE:\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582269547\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151006T080000\n" +
            "DTEND;TZID=Europe/Berlin:20151006T093000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TU\n" +
            "EXDATE:\n" +
            "LOCATION:G - 240\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150587264907\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220520 - Systemmodellierung\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:T094500\n" +
            "DTEND;TZID=Europe/Berlin:T111500\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TU\n" +
            "EXDATE:\n" +
            "LOCATION:F - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582264882\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151006T113000\n" +
            "DTEND;TZID=Europe/Berlin:20151006T130000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TU\n" +
            "EXDATE:\n" +
            "LOCATION:G - 240\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150580264873\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220720 - Programmiertechnik 2\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151006T140000\n" +
            "DTEND;TZID=Europe/Berlin:20151006T153000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TU\n" +
            "EXDATE:\n" +
            "LOCATION:G - 151\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582264880\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151006T154500\n" +
            "DTEND;TZID=Europe/Berlin:20151006T171500\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TU\n" +
            "EXDATE:\n" +
            "LOCATION:G - 151\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582264881\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151007T080000\n" +
            "DTEND;TZID=Europe/Berlin:20151007T093000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=WE\n" +
            "EXDATE:20160106T140000Z\n" +
            "LOCATION:F - 023\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150639265023\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220620 - Mathematik 2\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151007T140000\n" +
            "DTEND;TZID=Europe/Berlin:20151007T153000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=WE\n" +
            "EXDATE:20160106T140000Z\n" +
            "LOCATION:G - 149\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150580264871\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220720 - Programmiertechnik 2 (Bittel)\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151007T154500\n" +
            "DTEND;TZID=Europe/Berlin:20151007T171500\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=WE\n" +
            "EXDATE:20160106T140000Z\n" +
            "LOCATION:F - 035\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150587264906\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220520 - Systemmodellierung\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151007T173000\n" +
            "DTEND;TZID=Europe/Berlin:20151007T190000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=WE\n" +
            "EXDATE:20160106T140000Z\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150601264951\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220113 - Konsolidierung Grundlagen Mathematik\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151008T113000\n" +
            "DTEND;TZID=Europe/Berlin:20151008T130000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TH\n" +
            "EXDATE:\n" +
            "LOCATION:C - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150577270096\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220110 - Mathematik 1 | AIN\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151015T173000\n" +
            "DTEND;TZID=Europe/Berlin:20151015T190000\n" +
            "EXDATE:\n" +
            "LOCATION:F - 109\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150639268960\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220620 - Mathematik 2\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151008T080000\n" +
            "DTEND;TZID=Europe/Berlin:20151008T093000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TH\n" +
            "EXDATE:\n" +
            "LOCATION:G - 240\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150580264875\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220720 - Programmiertechnik 2 (Bittel)\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:T113000\n" +
            "DTEND;TZID=Europe/Berlin:T130000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TH\n" +
            "EXDATE:\n" +
            "LOCATION:F - 110\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150582264883\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220920 - Rechnerarchitekturen\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151008T140000\n" +
            "DTEND;TZID=Europe/Berlin:20151008T153000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TH\n" +
            "EXDATE:\n" +
            "LOCATION:G - 149\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150593264919\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220820 - Systemprogrammierung (Drachenfels)\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151008T173000\n" +
            "DTEND;TZID=Europe/Berlin:20151008T190000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=1;BYDAY=TH\n" +
            "EXDATE:20151015T140000Z,\n" +
            "LOCATION:G - 042\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150639265025\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220620 - Mathematik 2\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20151009T113000\n" +
            "DTEND;TZID=Europe/Berlin:20151009T130000\n" +
            "RRULE:FREQ=WEEKLY;UNTIL=20160129T235900Z;INTERVAL=2;BYDAY=FR\n" +
            "EXDATE:20160101T140000Z,\n" +
            "LOCATION:G - 240\n" +
            "DTSTAMP:20151115T174812Z\n" +
            "UID:150595264929\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:14220622 - Stochastik\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR\n";

    @Test
    public void testCorrectEvents() throws Exception {
        Globals.icsFileStream = Utils.stringToInputstream(file);
        CalenderValidator.CorrectEvents();
    }
}