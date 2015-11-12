package com.hstrobel.lsfplan.frags;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.hstrobel.lsfplan.CalenderUtils;
import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;

import net.fortuna.ical4j.model.component.VEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class MainListFragment extends ListFragment implements DatePickerDialog.OnDateSetListener {
    private List<ListViewItem> mItems;        // ListView items list
    private List<VEvent> evs;
    private Calendar cal;
    private ListViewDemoAdapter listadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize the items list
        mItems = new ArrayList<ListViewItem>();
        Resources resources = getResources();
        cal = Calendar.getInstance();

        // initialize and set the list adapter
        listadapter = new ListViewDemoAdapter(getActivity(), mItems);
        setListAdapter(listadapter);
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.listview_main, container, false);
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    private int mCounter = 0;
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        if (position == 0) {
            //Change day object
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog  .show();
        } else if (item.title.contains("Mathe")){
            if (mCounter > 3) Toast.makeText(getActivity(), "Mathe. Burn in hell. Slowly.", Toast.LENGTH_SHORT).show();
            mCounter++;
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        //setup
        if (Globals.myCal != null) {
            evs = CalenderUtils.GetEventsForDay(Globals.myCal, (Calendar)cal.clone());
            CalenderUtils.SortEvents(evs);

            SimpleDateFormat d = new SimpleDateFormat("E, dd MMMM yyyy");
            listadapter.clear();
            listadapter.add(new ListViewItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_black_24dp),
                    String.format(getString(R.string.main_lecture_day), d.format(cal.getTime())), 
                    getString(R.string.main_lecture_change)));

            for (VEvent ev : evs) {
                listadapter.add(new ListViewItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_black_24dp),
                        CalenderUtils.getTopic(ev),
                        CalenderUtils.formatEventShort(ev, getActivity())));
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        onResume(); //update
    }
}