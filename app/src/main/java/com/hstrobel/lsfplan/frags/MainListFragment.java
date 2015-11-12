package com.hstrobel.lsfplan.frags;

import android.content.res.Resources;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.hstrobel.lsfplan.CalenderUtils;
import com.hstrobel.lsfplan.Globals;
import com.hstrobel.lsfplan.R;

import net.fortuna.ical4j.model.component.VEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainListFragment extends ListFragment {
    private List<ListViewItem> mItems;        // ListView items list
    Collection<VEvent> evs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize the items list
        mItems = new ArrayList<ListViewItem>();
        Resources resources = getResources();

        // initialize and set the list adapter
        setListAdapter(new ListViewDemoAdapter(getActivity(), mItems));
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ListViewItem item = mItems.get(position);

        // do something
        Toast.makeText(getActivity(), item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //setup
        if (Globals.myCal != null) {
            evs = CalenderUtils.GetNextEvent(Globals.myCal);

            for (VEvent ev : evs) {
                mItems.add(new ListViewItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_info_black_24dp),
                        CalenderUtils.getTopic(ev),
                        CalenderUtils.formatEventShort(ev, getActivity())));
            }
        }
    }
}