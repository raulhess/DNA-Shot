package com.dnashot.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.dnashot.activities.InfoActivity;
import com.dnashot.activities.SettingsActivity;
import com.dnashot.activities.TakePictureActivity;
import com.dnashot.v0.R;

@SuppressLint("InflateParams")
public class DrawerActivity extends Activity implements
		ListView.OnItemClickListener {
	public DrawerLayout drawerLayout;
	public FrameLayout activityContent;
	public ListView drawerList;

	@Override
	public void setContentView(int layoutResID) {
		drawerLayout = (DrawerLayout) getLayoutInflater().inflate(
				R.layout.navigation_drawer_layout, null);
		activityContent = (FrameLayout) drawerLayout
				.findViewById(R.id.act_content);
		getLayoutInflater().inflate(layoutResID, activityContent, true);
		super.setContentView(drawerLayout);
	}

	public void createDrawerList() {
		String[] options = new String[] {
				getResources().getString(R.string.title_activity_take_picture),
				getResources()
						.getString(R.string.title_activity_upload_picture),
				getResources().getString(R.string.title_activity_info),
				getResources().getString(R.string.title_activity_settings) };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new DrawerAdapter(this, options));
		drawerList.setOnItemClickListener(this);
		
		drawerList.bringToFront();
		drawerLayout.requestLayout();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent it = null;
		switch (position) {
		case 0:
			it = new Intent(getApplicationContext(), TakePictureActivity.class);
			break;
		case 1:
			//it = new Intent(getApplicationContext(), UploadPictureActivity.class);
			break;
		case 2:
			it = new Intent(getApplicationContext(), InfoActivity.class);
			break;
		case 3:
			it = new Intent(getApplicationContext(), SettingsActivity.class);
			break;
		}
		
		drawerLayout.closeDrawer(drawerList);
		
		if(it != null)
			startActivity(it);
	}
}
