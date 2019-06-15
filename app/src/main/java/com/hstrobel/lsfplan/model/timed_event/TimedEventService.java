package com.hstrobel.lsfplan.model.timed_event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.model.NotificationUtils;

/**
 * Used to trigger actions on specific days, baked into the app
 */
public class TimedEventService extends JobIntentService {
    //TODO: dynmaic action callbacks would be nice

    //27.06 - 15.07
    public static TimedEvent[] eventList = {new TimedEvent("SS18_final_greeting", 1530100800, 1531648800),
            new TimedEvent("blameHTWGEvent", 0, 3376684800L)
            //, new TimedEvent("debugEvent", 0, 1531648800)
    };

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(Constants.TAG, "TimedEventService: try schedule");

        try {
            for (TimedEvent event : eventList) {
                long notBefore = event.notBefore * 1000;
                long notAfter = event.notAfter * 1000;

                if (notBefore < System.currentTimeMillis() && notAfter > System.currentTimeMillis()) {
                    //fire action,detect event
                    switch (event.name) {
                        case "SS18_final_greeting":
                        case "debugEvent":
                            OneTimeSettingExecutor.checkAndOneTimeExecute(GlobalState.getInstance().settings, "OneTimeSetting_" + event.name, () -> {
                                Log.i(Constants.TAG, "event fired");
                                NotificationUtils.praiseTheUser(this);
                            });
                        case "blameHTWGEvent":
                            OneTimeSettingExecutor.checkAndOneTimeExecute(GlobalState.getInstance().settings, "OneTimeSetting_" + event.name, () -> {
                                Log.i(Constants.TAG, "event blameHTWGEvent fired");
                                NotificationUtils.blameHTWG(this);
                            });
                    }
                }
            }

        } catch (Exception ex) {
            Log.e(Constants.TAG, "TimedEventService: ex ", ex);
        }



    }

    private static class TimedEvent {
        public String name;
        public long notBefore;
        public long notAfter;

        public TimedEvent(String name, long notBefore, long notAfter) {
            this.name = name;
            this.notBefore = notBefore;
            this.notAfter = notAfter;
        }
    }

}
