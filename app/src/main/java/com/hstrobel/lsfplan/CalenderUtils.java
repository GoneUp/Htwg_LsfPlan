package com.hstrobel.lsfplan;

import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.Collection;
import java.util.List;

/**
 * Created by Henry on 10.11.2015.
 */
public class CalenderUtils {
    public static Collection GetTodaysEvents(Calendar myCal) {
        java.util.Calendar today = java.util.Calendar.getInstance();
        //today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);

        // create a period starting now with a duration of one (1) day..
        Period period = new Period(new DateTime(today.getTime()), new Dur(1, 0, 0, 0));
        Filter filter = new Filter(new PeriodRule(period));

        Collection eventsToday = filter.filter(myCal.getComponents(Component.VEVENT));
        return eventsToday;
    }

    public static VEvent GetNextEvent(Calendar myCal){
        java.util.Calendar calEnd = java.util.Calendar.getInstance();
        calEnd.set(java.util.Calendar.YEAR, 3000);
        VEvent toReturn = null;

        Collection events = GetTodaysEvents(myCal);
        for (Object comp : events) {
            if (!(comp instanceof VEvent)) continue;

            VEvent event = (VEvent) comp;
            if (toReturn == null) {
                //first element
                toReturn = event;
            } else
            {
                //compare to get the start datw wich is a) in future b) the closest
                Date startE =  GetNextRecuringStartDate(event);
                Date starttR =  GetNextRecuringStartDate(toReturn);

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

    public static DateTime GetNextRecuringStartDate(VEvent event){
        DateTime start = new DateTime();

        Period period = new Period(new DateTime(start.getTime()), new Dur(14, 0, 0, 0));
        PeriodList r = event.calculateRecurrenceSet(period);

        Period closest = null;
        for (Period p : (Iterable<Period>) r) {
            System.out.println(" - " + p.toString());
            if (closest == null){
                closest = p;
            } else {
                if (p.compareTo(closest) < 0){
                    closest = p;
                }
            }
        }

        return closest.getStart();
    }

    public static String formatEvent(VEvent event){
        String topic = event.getSummary().getValue().split("-")[1].substring(1);
        java.util.Date time = GetNextRecuringStartDate(event);
        String room = event.getLocation().getValue();
        return String.format("Topic: %s - Time: %tR - Room: %s", topic, time, room);
    }
}
