package com.hstrobel.lsfplan.frags;

import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.hstrobel.lsfplan.R;
import com.hstrobel.lsfplan.classes.EventCache;
import com.hstrobel.lsfplan.classes.Globals;
import com.hstrobel.lsfplan.classes.NotificationUtils;
import com.hstrobel.lsfplan.classes.gui.EventItem;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MainListFragment extends ListFragment implements DatePickerDialog.OnDateSetListener {
    private List<EventItem> mItems;        // ListView items list
    private EventListAdapter listadapter;

    private Calendar selectedDay;
    private EventCache cache;
    private int eastereggCounter = 0;



    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_listview, container, false);
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("LSF", "MainListFragment:onCreate");
        // initialize the items list
        mItems = new ArrayList<EventItem>();
        Resources resources = getResources();
        selectedDay = Calendar.getInstance();
        cache = new EventCache();

        // initialize and set the list adapter
        listadapter = new EventListAdapter(getActivity(), mItems);
        setListAdapter(listadapter);
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
            if (item.title.contains("Mathe")) {
                if (eastereggCounter > 3)
                    Toast.makeText(getActivity(), "Mathe. Burn in hell. Slowly.", Toast.LENGTH_SHORT).show();
                eastereggCounter++;
            } else {
                //default case
                StringBuilder message = new StringBuilder();
                message.append(String.format(getString(R.string.main_topic), NotificationUtils.getTopic(ev)));
                message.append(String.format(getString(R.string.main_startdate), NotificationUtils.formatDate(ev)));

                //Repeating events
                if (ev.getProperties(Property.RRULE).size() > 0){
                    RRule rule = (RRule) ev.getProperties(Property.RRULE).get(0);
                    message.append(String.format(getString(R.string.main_recurring), rule.getRecur().getFrequency().replace("WEEKLY", getString(R.string.main_weekly))));
                }

                message.append(String.format(getString(R.string.main_comment), ""));

                AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setMessage(message);
                ad.setPositiveButton("Ok", null);
                ad.create().show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //setup
        try {
            if (Globals.myCal != null) {
                List<VEvent> evs = cache.getDay((Calendar) selectedDay.clone());

                SimpleDateFormat d = new SimpleDateFormat("E, dd MMMM yyyy", Locale.GERMANY);
                Drawable icon_book = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action);
                Drawable icon_left = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_left);
                Drawable icon_right = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_right);
                listadapter.clear();
                listadapter.add(new EventItem(icon_left, icon_right,
                        String.format(getString(R.string.main_lecture_day), d.format(selectedDay.getTime())),
                        getString(R.string.main_lecture_change), this, null));

                for (VEvent ev : evs) {
                    listadapter.add(new EventItem(icon_book, null,
                            NotificationUtils.getTopic(ev),
                            NotificationUtils.formatEventShort(ev, getActivity()), this, ev));
                }
            }
        } catch (Exception ex) {
            Log.e("LSF", "FAIL onResume:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL onResume ST:\n " + ExceptionUtils.getFullStackTrace(ex));
            Toast.makeText(getActivity(), "Loading failed! Resetting the app may help.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDay.set(Calendar.YEAR, year);
        selectedDay.set(Calendar.MONTH, monthOfYear);
        selectedDay.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        onResume(); //update
    }

    public void onDateInc() {
        selectedDay.add(Calendar.DAY_OF_MONTH, 1);
        onResume(); //update
    }

    public void onDateDec() {
        selectedDay.add(Calendar.DAY_OF_MONTH, -1);
        onResume(); //update
    }

    public void onDateReset() {
        selectedDay = new GregorianCalendar();
        onResume(); //update
    }

    public void onCheckCache() {
        cache.generateFullCache((Calendar) selectedDay.clone());
    }
}