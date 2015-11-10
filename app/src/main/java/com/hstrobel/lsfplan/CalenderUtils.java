package com.hstrobel.lsfplan;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.Collection;
import java.util.List;

/**
 * Created by Henry on 10.11.2015.
 */
public class CalenderUtils {
    public static Collection GetTodaysEvents(Calendar myCal) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);

        // create a period starting now with a duration of one (1) day..
        Period period = new Period(new DateTime(today.getTime()), new Dur(1, 0, 0, 0));
        Filter filter = new Filter(new PeriodRule(period));

        Collection eventsToday = filter.filter(myCal.getComponents(Component.VEVENT));
        return eventsToday;
    }

    public static VEvent GetNextEvent(Calendar myCal){
        VEvent toReturn = null;
        for (Object comp :myCal.getComponents()) {
            if (!(comp instanceof VEvent)) continue;

            VEvent event = (VEvent) comp;
            if (toReturn == null) {
                //first element
                toReturn = event;
            } else
            {
                //compare to get the start datw wich is a) in future b) the closest
                Date startE = event.getStartDate().getDate();
                Date starttR = toReturn.getStartDate().getDate();

                //past check
                if (startE.before(new java.util.Date())) continue;

                if (startE.before(starttR)){
                    //closer
                    toReturn = event;
                }
            }

        }

        return toReturn;
    }
}
