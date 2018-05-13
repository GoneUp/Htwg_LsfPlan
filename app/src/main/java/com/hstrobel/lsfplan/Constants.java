package com.hstrobel.lsfplan;

/**
 * Created by Henry on 27.03.2017.
 */

public class Constants {

    //IDs
    public static final String INTENT_UPDATE_LIST = "INTENT_UPDATE_LIST";
    public static final String INTENT_EXTRA_REFRESH = "INTENT_EXTRA_REFRESH";
    public static final int SYNC_SERVICE_ID = 133742;
    public static final String NOTIFICATION_CHANNEL_ALARMS_ID = "lsf_alarms";
    public static final String NOTIFICATION_CHANNEL_REMOTE_ID = "lsf_remote";

    //Firebase
    public static final String FB_PROP_CATEGORY = "course_category";
    public static final String FB_PROP_SPECIFIC = "course_specific";
    public static final int FB_MAX_LENGTH = 99;
    public static final String FB_CONTENT_NOTIFY = "notify";
    public static final String FB_CONTENT_DL = "download";

    //Settings
    public static final String PREF_COLLEGE = "college";
    public static final int MODE_HTWG = 0;
    public static final int MODE_UNI_KN = 1;
    public static final int PREF_COLLEGE_DEFAULT = MODE_HTWG;
    public static final String PREF_FLAG_KEYSTORE = "flagUseKeystore";
    public static final String PREF_DEV_NOTIFY = "debugNotify";
    public static final String PREF_DEV_SYNC = "debugSync";

    public static final String PREF_LOGIN_AUTOSAVE = "loginAutoSave";
    public static final String PREF_LOGIN_USER = "loginUser";
    public static final String PREF_LOGIN_PASSWORD = "loginPassword";

    //Model stuff, misc
    public static final String MAGIC_WORD_LOGIN = "#LOGIN#";
    public static final int NETWORK_TIMEOUT = 10 * 1000;
    public static final String NETWORK_USERAGENT = "Android_lsfapp";
    public static final String CRYPTO_KEY_NAME = "KEY_DEFAULT";
    public static final int CRYPTO_KEY_SIZE = 2048;


    public static final String CONTENT_TESTMODE_PLAN = "BEGIN:VCALENDAR\n" +
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
            "\n" +
            "METHOD:PUBLISH\n" +
            "\n" +
            "BEGIN:VEVENT\n" +
            "DTSTART;TZID=Europe/Berlin:20101004T080000\n" +
            "DTEND;TZID=Europe/Berlin:20201004T093000\n" +
            "RRULE:FREQ=MINUTELY;INTERVAL=15;UNTIL=20200126T235900Z\n" +
            "LOCATION:Test\n" +
            "DTSTAMP:20100928T203147Z\n" +
            "UID:123456789\n" +
            "DESCRIPTION:\n" +
            "SUMMARY:Test Schedule, every 15 mins\n" +
            "CATEGORIES:Vorlesung/Übung\n" +
            "END:VEVENT\n" +
            "END:VCALENDAR\n";

}
