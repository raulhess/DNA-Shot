package com.dnareader.system;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnareader.data.Result;
import com.dnareader.v0.R;

public class ResultsAdapter extends ArrayAdapter<Result> {
	private final Activity context;
	private final List<Result> list;
	private final List<Bitmap> imageList = new ArrayList<Bitmap>();

	public ResultsAdapter(Activity context, List<Result> list) {
		super(context, R.layout.result_cell, list);
		this.context = context;
		this.list = list;
		
		for(Result r : list){
			imageList.add(BitmapFactory.decodeByteArray(r.getThumbnail(), 0, r.getThumbnail().length));
		}
		
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View cellView = inflater.inflate(R.layout.result_cell, null, true);
		ImageView img = (ImageView) cellView.findViewById(R.id.img);
		img.setImageBitmap(imageList.get(position));
		TextView id = (TextView) cellView.findViewById(R.id.id);
		id.setText(context.getResources().getString(R.string.result_title) + list.get(position).getId());
		TextView status = (TextView) cellView.findViewById(R.id.status);
		status.setText("Status: " + context.getResources().getString(R.string.result_error));
		cellView.setBackgroundColor(context.getResources().getColor(R.color.app_soft_red));
		switch (list.get(position).getState()) {
		case Result.UNPROCESSED:
			status.setText("Status: " + context.getResources().getString(R.string.result_not_sent));
			cellView.setBackgroundColor(context.getResources().getColor(R.color.app_white));
			break;
		case Result.OCR_PROCESSED:
			status.setText("Status: " + context.getResources().getString(R.string.result_waiting));
			cellView.setBackgroundColor(context.getResources().getColor(R.color.app_white));
			break;
		case Result.DONE:
			status.setText("Status: " + context.getResources().getString(R.string.result_done));
			if(list.get(position).isChecked())
				cellView.setBackgroundColor(context.getResources().getColor(R.color.app_soft_blue));
			else
				cellView.setBackgroundColor(context.getResources().getColor(R.color.app_blue));
			break;
		}

		return cellView;
	}

}
