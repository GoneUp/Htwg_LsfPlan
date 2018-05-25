package com.hstrobel.lsfplan.gui.download;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.hstrobel.lsfplan.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Henry on 02.12.2015.
 */
public class CourseListAdapter implements ExpandableListAdapter {
    private List<CourseGroup> list;
    private Context mContext;

    public CourseListAdapter(Context mContext) {
        list = new LinkedList<>();
        this.mContext = mContext;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);

        TextView text = convertView.findViewById(android.R.id.text1);
        text.setTypeface(null, Typeface.BOLD);
        CourseGroup group = list.get(groupPosition);
        text.setText(group.name);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(R.layout.planview_item, parent, false);

        TextView text = convertView.findViewById(R.id.planText);
        CourseGroup group = list.get(groupPosition);
        text.setText(" -- " + group.items.get(childPosition).name);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
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

    public void clear() {
        list.clear();
    }

    public void addPlanGroup(String name) {
        CourseGroup group = new CourseGroup(name);
        list.add(group);
    }

    public void addPlanItem(String groupName, String name, String url) {
        CourseGroup group = null;
        for (CourseGroup g : list) {
            if (Objects.equals(g.name, groupName)) {
                group = g;
                break;
            }
        }
        if (group == null) return;

        CourseGroup.Course item = new CourseGroup.Course(name, url);
        group.items.add(item);
    }


}
