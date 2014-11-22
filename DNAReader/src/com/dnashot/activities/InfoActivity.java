package com.dnashot.activities;

import com.dnareader.v0.R;
import com.dnashot.system.DrawerActivity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class InfoActivity extends DrawerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_info);
		createDrawerList();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}
}