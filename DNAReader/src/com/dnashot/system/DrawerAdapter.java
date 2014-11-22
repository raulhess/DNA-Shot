package com.dnashot.system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dnashot.v0.R;

public class DrawerAdapter extends ArrayAdapter<String> {
	private Context context;
	private String[] options;

	public DrawerAdapter(Context context, String[] options) {
		super(context, 0, options);
		this.context = context;
		this.options = options;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.navigation_drawer_item,
					parent,false);
		}
		ImageView img = (ImageView) convertView.findViewById(R.id.drawer_img);
		TextView txt = (TextView) convertView.findViewById(R.id.drawer_txt);

		String option = options[position];
		txt.setText(option);
		if (option.equals(context.getResources().getString(
				R.string.title_activity_take_picture)))
			img.setBackground(context.getResources().getDrawable(
					android.R.drawable.ic_menu_camera));
		if (option.equals(context.getResources().getString(
				R.string.title_activity_upload_picture)))
			img.setBackground(context.getResources().getDrawable(
					android.R.drawable.ic_menu_upload));
		if (option.equals(context.getResources().getString(
				R.string.title_activity_results)))
			img.setBackground(context.getResources().getDrawable(
					android.R.drawable.ic_menu_agenda));
		if (option.equals(context.getResources().getString(
				R.string.title_activity_info)))
			img.setBackground(context.getResources().getDrawable(
					android.R.drawable.ic_menu_info_details));
		if (option.equals(context.getResources().getString(
				R.string.title_activity_settings)))
			img.setBackground(context.getResources().getDrawable(
					android.R.drawable.ic_menu_preferences));
		return convertView;
	}

}
