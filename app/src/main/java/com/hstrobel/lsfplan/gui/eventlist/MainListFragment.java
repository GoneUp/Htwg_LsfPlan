package com.hstrobel.lsfplan.gui.eventlist;

import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hstrobel.lsfplan.Constants;
import com.hstrobel.lsfplan.GlobalState;
import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.model.NotificationUtils;
import com.hstrobel.lsfplan.model.calender.EventCache;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class MainListFragment extends ListFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "LSF";

    private AdView adView;

    private List<EventItem> mItems;        // ListView items list
    private EventListAdapter listAdapter;

    private GlobalState state = GlobalState.getInstance();
    private Calendar selectedDay;
    private EventCache cache;
    private boolean skipWeekend = false;
    private boolean skipOnlyEmptyDays = false;

    private int eastereggCounter = 0;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;
            ;
            if (intent.getAction().equals(Constants.INTENT_UPDATE_LIST)) {
                getActivity().runOnUiThread(() -> {
                            updateContent();
                            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.main_calendar_updated, Snackbar.LENGTH_SHORT).show();
                        }
                );
            }
        }
    };
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdFailedToLoad(int i) {
            super.onAdFailedToLoad(i);
            //hide ad
            adView.setVisibility(View.GONE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("LSF", "MainListFragment:onCreate");
        // initialize the items list
        mItems = new ArrayList<>();
        selectedDay = Calendar.getInstance();
        cache = new EventCache(true);

        // initialize and set the list adapter
        listAdapter = new EventListAdapter(getActivity(), mItems);
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_listview, container, false);

        initAd(view);
        return view;
    }

    private boolean areAdsEnabled() {
        return state.settings.getBoolean("enableAds", false);
    }

    private void initAd(View view) {
        //Ads
        adView = (AdView) view.findViewById(R.id.adView);
        adView.setAdListener(adListener);
        adView.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        Log.d("LSF", "MainListFragment:onCreateView");
        getListView().setDivider(null);

        getListView().setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                //+1
                onDateInc();
            }

            @Override
            public void onSwipeRight() {
                //-1
                onDateDec();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_UPDATE_LIST));
        updateContent();

        skipWeekend = state.settings.getBoolean("skipWeekend", false);
        skipOnlyEmptyDays = state.settings.getBoolean("skipWeekendDaysWithoutEvents", false);

        //Ads meh
        if (areAdsEnabled()) {
            MobileAds.initialize(getActivity().getApplicationContext(), getString(R.string.firebase_id));
            MobileAds.setAppMuted(true);

            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("2FF92E008889C6976B3F697DE3CB318A") //find 7
                    .addTestDevice("E624D76F3DFE84D3E8E20B6C33C4A7C5")
                    .addTestDevice("355FC8B9280C4AD8481AB322B403A089") //6x stock
                    .build();
            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        } else {
            adView.setVisibility(View.GONE);
        }

        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(broadcastReceiver);
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adView != null) {
            adView.destroy();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        EventItem item = mItems.get(position);
        VEvent ev = item.sourceEvent;
        Log.d("LSF", String.valueOf(v.getId()));

        if (position == 0) {
            //Change day object
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, selectedDay.get(Calendar.YEAR), selectedDay.get(Calendar.MONTH), selectedDay.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else {
            boolean isMathe = item.title.contains("Mathe");

            StringBuilder message = new StringBuilder();
            message.append(String.format(getString(R.string.main_topic), NotificationUtils.getTopic(ev)));
            message.append(String.format(getString(R.string.main_startdate), NotificationUtils.formatDate(ev)));

            //Repeating events
            if (ev.getProperties(Property.RRULE).size() > 0) {
                RRule rule = (RRule) ev.getProperties(Property.RRULE).get(0);
                message.append(String.format(getString(R.string.main_recurring), rule.getRecur().getFrequency().replace("WEEKLY", getString(R.string.main_weekly))));
            }

            message.append(String.format(getString(R.string.main_comment), ""));

            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
            ad.setMessage(message);
            ad.setPositiveButton("Ok", null);
            if (isMathe) {
                ad.setNeutralButton("\uD83D\uDE10", (dialogInterface, i) -> {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Mathe. Burn in hell. Slowly.", Snackbar.LENGTH_SHORT).show();
                });
            }

            ad.create().show();

            if (isMathe) {
                eastereggCounter++;
            }
        }
    }

    private void updateContent() {
        //setup
        try {
            if (state.myCal != null) {
                List<VEvent> evs = cache.getDay(selectedDay);

                DateFormat d = SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL);
                Drawable icon_book = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action);
                Drawable icon_left = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_left);
                Drawable icon_right = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_right);
                listAdapter.clear();
                listAdapter.add(new EventItem(icon_left, icon_right,
                        String.format(getString(R.string.main_lecture_day), d.format(selectedDay.getTime())),
                        getString(R.string.main_lecture_change), this, null));

                for (VEvent ev : evs) {
                    listAdapter.add(new EventItem(icon_book, null,
                            NotificationUtils.getTopic(ev),
                            NotificationUtils.formatEventReminderShort(ev, getActivity()), this, ev));
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "List updateContent: ", ex);
            Toast.makeText(getActivity(), "Loading failed! Resetting the app may help.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDay.set(Calendar.YEAR, year);
        selectedDay.set(Calendar.MONTH, monthOfYear);
        selectedDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateContent();
    }

    public void onDateInc() {
        selectedDay.add(Calendar.DAY_OF_MONTH, 1);
        if (skipWeekend) {
            int day = selectedDay.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                while (selectedDay.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    //dont skip if we see events
                    if (skipOnlyEmptyDays && cache.getDay(selectedDay).size() > 0) {
                        break;
                    }

                    selectedDay.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }
        updateContent();
    }


    public void onDateDec() {
        selectedDay.add(Calendar.DAY_OF_MONTH, -1);
        if (skipWeekend) {
            int day = selectedDay.get(Calendar.DAY_OF_WEEK);
            if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
                while (selectedDay.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                    //dont skip if we see events
                    if (skipOnlyEmptyDays && cache.getDay(selectedDay).size() > 0) {
                        break;
                    }

                    selectedDay.add(Calendar.DAY_OF_MONTH, -1);
                }
            }
        }
        updateContent();
    }

    public void onDateReset() {
        selectedDay = new GregorianCalendar();
        updateContent();
    }
}