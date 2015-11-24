package com.hstrobel.lsfplan.frags;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hstrobel.lsfplan.R;

import java.util.List;

public class ListViewDemoAdapter extends ArrayAdapter<ListViewItem> {

    public ListViewDemoAdapter(Context context, List<ListViewItem> items) {
        super(context, R.layout.listview_item, items);
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            if (position == 0) {
                convertView = inflater.inflate(R.layout.listview_item_head, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.listview_item, parent, false);
            }


            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.IconLeft);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            if (convertView.findViewById(R.id.IconRight) != null)
                viewHolder.ivIcon2 = (ImageView) convertView.findViewById(R.id.IconRight);

            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ListViewItem item = getItem(position);
        viewHolder.ivIcon.setImageDrawable(item.icon);
        viewHolder.ivIcon.setTag(item.fragment);
        if (viewHolder.ivIcon2 != null) {
            viewHolder.ivIcon2.setImageDrawable(item.icon2);
            viewHolder.ivIcon2.setTag(item.fragment);
        }
        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvDescription.setText(item.description);


        if (position == 0) {
            viewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainListFragment frag = (MainListFragment) v.getTag();
                    frag.onDateDec();

                }

            });
            viewHolder.ivIcon2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainListFragment frag = (MainListFragment) v.getTag();
                    frag.onDateInc();
                }

            });
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        ImageView ivIcon2;
        TextView tvTitle;
        TextView tvDescription;
    }
}