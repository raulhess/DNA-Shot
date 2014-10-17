package com.dnareader.activities;


import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.dnareader.system.DrawerActivity;
import com.dnareader.v0.R;

public class UploadPictureActivity extends DrawerActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_upload_picture);
		createDrawerList();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}
}
