package com.hstrobel.lsfplan;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Henry on 02.12.2015.
 */
public class PlanListAdapter implements ExpandableListAdapter {
    private class PlanItem {
        public String URL;
        public String name;
    }
    private class PlanGroup {
        public String name;
        public List<PlanItem> items = new LinkedList<>();
    }

    private List<PlanGroup> list;


    public PlanListAdapter(){
        list = new LinkedList<>();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public int getGroupCount() {
        return list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition <= list.size()) return list.get(groupPosition).items.size();
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition <= list.size()) return list.get(groupPosition);
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (groupPosition <= list.size())
            if (childPosition <= list.get(groupPosition).items.size())
                return list.get(groupPosition).items.get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from();
        convertView = inflater.inflate(R.layout.listview_item, parent, false);

        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }
}
