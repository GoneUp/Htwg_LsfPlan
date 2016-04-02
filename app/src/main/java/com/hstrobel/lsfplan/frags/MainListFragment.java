package com.hstrobel.lsfplan.frags;

import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
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
import com.hstrobel.lsfplan.classes.CalenderUtils;
import com.hstrobel.lsfplan.classes.EventItem;
import com.hstrobel.lsfplan.classes.Globals;

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
    private Calendar cal;
    private EventListAdapter listadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("LSF", "MainListFragment:onCreate");
        // initialize the items list
        mItems = new ArrayList<EventItem>();
        Resources resources = getResources();
        cal = Calendar.getInstance();

        // initialize and set the list adapter
        listadapter = new EventListAdapter(getActivity(), mItems);
        setListAdapter(listadapter);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_listview, container, false);
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        Log.d("LSF", "MainListFragment:onCreateView");
        getListView().setDivider(null);
    }

    private int mCounter = 0;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        EventItem item = mItems.get(position);
        VEvent ev = item.sourceEvent;
        Log.d("LSF", String.valueOf(v.getId()));

        if (position == 0) {
            //Change day object
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        } else {
            if (item.title.contains("Mathe")) {
                if (mCounter > 3)
                    Toast.makeText(getActivity(), "Mathe. Burn in hell. Slowly.", Toast.LENGTH_SHORT).show();
                mCounter++;
            } else {
                //default case
                StringBuilder message = new StringBuilder();
                message.append(String.format(getString(R.string.main_topic), CalenderUtils.getTopic(ev)));
                message.append(String.format(getString(R.string.main_startdate), CalenderUtils.formatDate(ev)));

                //Repeating events
                if (ev.getProperties(Property.RRULE).size() > 0){
                    RRule rule = (RRule) ev.getProperties(Property.RRULE).get(0);
                    message.append(String.format(getString(R.string.main_recurring), rule.getRecur().getFrequency().replace("WEEKLY", getString(R.string.main_weekly))));
                }

                message.append(String.format(getString(R.string.main_comment), ""));

                final AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
                ad.setCancelable(false); // This blocks the 'BACK' button
                ad.setMessage(message);
                ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad.show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //setup
        try {
            if (Globals.myCal != null) {
                List<VEvent> evs = CalenderUtils.getEventsForDay(Globals.myCal, (Calendar) cal.clone());
                CalenderUtils.sortEvents(evs);

                SimpleDateFormat d = new SimpleDateFormat("E, dd MMMM yyyy", Locale.GERMANY);
                Drawable icon_book = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action);
                Drawable icon_left = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_left);
                Drawable icon_right = ContextCompat.getDrawable(getActivity(), R.drawable.ic_action_right);
                listadapter.clear();
                listadapter.add(new EventItem(icon_left, icon_right,
                        String.format(getString(R.string.main_lecture_day), d.format(cal.getTime())),
                        getString(R.string.main_lecture_change), this, null));

                for (VEvent ev : evs) {
                    listadapter.add(new EventItem(icon_book, null,
                            CalenderUtils.getTopic(ev),
                            CalenderUtils.formatEventShort(ev, getActivity()), this, ev));
                }
            }
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "Loading failed! Resetting the app may help.", Toast.LENGTH_SHORT).show();
            Log.e("LSF", "FAIL onResume:\n " + ExceptionUtils.getCause(ex));
            Log.e("LSF", "FAIL onResume ST:\n " + ExceptionUtils.getFullStackTrace(ex));
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        onResume(); //update
    }

    public void onDateInc() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        onResume(); //update
    }

    public void onDateDec() {
        cal.add(Calendar.DAY_OF_MONTH, -1);
        onResume(); //update
    }


    public void onDateReset() {
        cal = new GregorianCalendar();
        onResume(); //update
    }

}