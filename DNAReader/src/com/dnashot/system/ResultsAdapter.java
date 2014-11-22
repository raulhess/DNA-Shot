package com.dnashot.system;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnashot.data.Result;
import com.dnashot.v0.R;

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
		if (list.get(position).getThumbnail() != null)
			img.setImageBitmap(list.get(position).getThumbnail());
		TextView id = (TextView) cellView.findViewById(R.id.id);
		id.setText(context.getResources().getString(R.string.result_title)
				 + list.get(position).getId());
		TextView status = (TextView) cellView.findViewById(R.id.status);
		status.setText("Status: "
				+ context.getResources().getString(R.string.result_error));
		ImageView ocrIcon = (ImageView) cellView.findViewById(R.id.ocr_icon);
		ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_error));
		ImageView dnaIcon = (ImageView) cellView.findViewById(R.id.dna_icon);
		dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_error));
		cellView.setBackgroundColor(context.getResources().getColor(
				R.color.app_soft_red));
		
		switch (list.get(position).getState()) {
		case Result.UNPROCESSED:
			status.setText("Status: "
					+ context.getResources().getString(R.string.UNPROCESSED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_off));
			ocrIcon.setAlpha(0.5F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(0.5F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.PREPROCESSING_STARTED:
			status.setText("Status: "
					+ context.getResources().getString(
							R.string.PREPROCESSING_STARTED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_off));
			ocrIcon.setAlpha(0.5F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(0.5F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.PREPROCESSING_FINISHED:
			status.setText("Status: "
					+ context.getResources().getString(
							R.string.PREPROCESSING_FINISHED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_off));
			ocrIcon.setAlpha(0.5F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(0.5F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.OCR_STARTED:
			status.setText("Status: "
					+ context.getResources().getString(R.string.OCR_STARTED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_off));
			ocrIcon.setAlpha(1F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(0.5F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.OCR_FINISHED:
			status.setText("Status: "
					+ context.getResources().getString(R.string.OCR_FINISHED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_on));
			ocrIcon.setAlpha(1F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(0.5F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.BLAST_STARTED:
			status.setText("Status: "
					+ context.getResources().getString(R.string.BLAST_STARTED));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_on));
			ocrIcon.setAlpha(1F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_off));
			dnaIcon.setAlpha(1F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_white));
			break;
		case Result.DONE:
			status.setText("Status: "
					+ context.getResources().getString(R.string.DONE));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_on));
			ocrIcon.setAlpha(1F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_on));
			dnaIcon.setAlpha(1F);
			cellView.setBackgroundColor(context.getResources().getColor(
						R.color.app_soft_blue));
			break;

		case Result.ERROR_OCR:
			status.setText("Status: "
					+ context.getResources().getString(R.string.ERROR_OCR));
			ocrIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.eye_error));
			ocrIcon.setAlpha(1F);
			dnaIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.dna_error));
			dnaIcon.setAlpha(1F);
			cellView.setBackgroundColor(context.getResources().getColor(
					R.color.app_soft_red));
		}

		return cellView;
	}

}
