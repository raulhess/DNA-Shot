package com.dnareader.system;

import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import com.dnareader.data.Hit;
import com.dnareader.v0.R;

public class HitsAdapter extends BaseExpandableListAdapter {
	Context context;
	List<Hit> hits;
	
	public HitsAdapter(Context context, List<Hit> hits) {
		this.context = context;
		this.hits = hits;
	}

	@Override
	public Object getChild(int arg0, int arg1) {
		return hits.get(arg0).getHsps().get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		return arg1;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
//	    final String childText = (String) getChild(groupPosition, childPosition);
	    
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
            		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.hits_item, null);
        }
 
//        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
 
//        txtListChild.setText(childText);
        return convertView;
		
	}

	@Override
	public int getChildrenCount(int arg0) {
		return hits.get(arg0).getHsps().size();
	}

	@Override
	public Object getGroup(int arg0) {
		return hits.get(arg0);
	}

	@Override
	public int getGroupCount() {
		return hits.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.hits_header, null);
        }
 
        TextView title = (TextView) convertView.findViewById(R.id.hit_header_def);
        title.setText(hits.get(groupPosition).getHit_def());
        TextView subTitle = (TextView) convertView.findViewById(R.id.hit_header_len);
        subTitle.setText("Number of Alignments: " + hits.get(groupPosition).getHit_len());
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
