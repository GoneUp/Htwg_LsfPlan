package com.hstrobel.lsfplan.model.job;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.model.NotificationUtils;
import com.hstrobel.lsfplan.model.calender.CalenderUtils;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BriefingJob extends DailyJob {
    public static final String TAG = "BriefingJob";
    public static int lastJobID = -1;

    public static void schedule() {
        //in case we have a leftover
        cancelJob();

        // schedule based on user setting with +/- a few mins margin for doze
        int briefingTime = GlobalState.getInstance().settings.getInt(Constants.PREF_BRIEFING_TIME, 22 * 60);
        Log.i(TAG, String.format("schedule briefing %d:%d", briefingTime / 60, briefingTime % 60));

        long baseTime = TimeUnit.HOURS.toMillis(briefingTime / 60) + TimeUnit.MINUTES.toMillis(briefingTime % 60);
        long range = TimeUnit.MINUTES.toMillis(2);

        //
        //DailyJob.startNowOnce(new JobRequest.Builder(TAG));
        lastJobID = DailyJob.schedule(new JobRequest.Builder(TAG), baseTime - range, baseTime + range);
        Log.i(TAG, "schedule id is" + lastJobID);
        Log.i(TAG, "all sched jobs " + JobManager.instance().getAllJobRequests());
        Log.i(TAG, "all done jobs " + JobManager.instance().getAllJobResults());
    }

    public static void cancelJob() {
        if (lastJobID != -1)
            JobManager.instance().cancel(lastJobID);
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(Params params) {
        Log.i(TAG, "onRunDailyJob: briefing fired");
        Calendar cal = new GregorianCalendar();
        //goto next day if we are in afternoon/evening
        if (cal.get(Calendar.HOUR_OF_DAY) > 8) {
            cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);

        List<VEvent> list = CalenderUtils.getEventsForDay(GlobalState.getInstance().myCal, cal);
        CalenderUtils.sortEvents(list);

        if (!list.isEmpty()) {
            NotificationUtils.showBriefingNotification(list, getContext());

        }
        return DailyJobResult.SUCCESS;
    }
}