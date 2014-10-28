package com.dnareader.system;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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

	public ResultsAdapter(Activity context, List<Result> list) {
		super(context, R.layout.result_cell, list);
		this.context = context;
		this.list = list;
		
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View cellView = inflater.inflate(R.layout.result_cell, null, true);
		ImageView img = (ImageView) cellView.findViewById(R.id.img);
		img.setImageBitmap(BitmapFactory.decodeByteArray(list.get(position)
				.getThumbnail(), 0, list.get(position).getThumbnail().length));
		TextView id = (TextView) cellView.findViewById(R.id.id);
		id.setText(context.getResources().getString(R.string.result_title)
				+ list.get(position).getId());
		TextView status = (TextView) cellView.findViewById(R.id.status);
		status.setText("Status: "
				+ context.getResources().getString(R.string.result_error));
		cellView.setBackgroundColor(context.getResources().getColor(
				R.color.app_soft_red));
		switch (list.get(position).getState()) {
		case Result.UNPROCESSED:
			status.setText("Status: "
					+ context.getResources()
							.getString(R.string.UNPROCESSED));
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.OCR_PROCESSING:
			status.setText("Status: "
					+ context.getResources().getString(R.string.OCR_PROCESSING));
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;	
		case Result.OCR_PROCESSED:
			status.setText("Status: "
					+ context.getResources().getString(R.string.OCR_PROCESSED));
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;		
		case Result.BLAST_PROCESSING:
			status.setText("Status: "
					+ context.getResources().getString(R.string.BLAST_PROCESSING));
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;		
		case Result.DONE:
			status.setText("Status: "
					+ context.getResources().getString(R.string.DONE));
			if (list.get(position).isChecked())
				cellView.setBackgroundColor(context.getResources().getColor(
						R.color.app_soft_blue));
			else
				cellView.setBackgroundColor(context.getResources().getColor(
						R.color.app_blue));
			break;
			
		case Result.ERROR:
			status.setText("Status: "
					+ context.getResources().getString(R.string.ERROR));
			if (list.get(position).isChecked())
				cellView.setBackgroundColor(context.getResources().getColor(
						R.color.app_soft_red));
			else
				cellView.setBackgroundColor(context.getResources().getColor(
						R.color.app_red));
			break;
		}

		return cellView;
	}

}
