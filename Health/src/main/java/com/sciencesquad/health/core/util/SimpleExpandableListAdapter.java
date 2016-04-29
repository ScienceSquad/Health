package com.sciencesquad.health.core.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class SimpleExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private String[][] mContents;
    private String[] mTitles;

    public SimpleExpandableListAdapter(Context context, String[] titles, String[][] contents) {
        super();
        if(titles.length != contents.length) {
            throw new IllegalArgumentException("Titles and Contents must be the same size.");
        }

        mContext = context;
        mContents = contents;
        mTitles = titles;

        inflater = LayoutInflater.from(context);
    }
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mContents[groupPosition][childPosition];
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    @Override
    public View getRealChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView row = (TextView)convertView.findViewById(android.R.id.text1);
        row.setText("\t" + mContents[groupPosition][childPosition]);
        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return mContents[groupPosition].length;
    }
    @Override
    public String[] getGroup(int groupPosition) {
        return mContents[groupPosition];
    }
    @Override
    public int getGroupCount() {
        return mContents.length;
    }
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }

        TextView row = (TextView) convertView.findViewById(android.R.id.text1);
        row.setTypeface(Typeface.DEFAULT_BOLD);
        row.setText(mTitles[groupPosition]);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}