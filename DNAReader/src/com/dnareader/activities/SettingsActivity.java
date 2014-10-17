package com.dnareader.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

import com.dnareader.system.DrawerActivity;
import com.dnareader.v0.R;

public class SettingsActivity extends DrawerActivity {
	private CheckBox notifications;
	private Spinner language;
	private SharedPreferences settings;
	
	private boolean sendNotifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		settings = getSharedPreferences(MainActivity.SETTINGS_FILE, 0);
		sendNotifications = settings.getBoolean("notifications", false);
		
		language = (Spinner) findViewById(R.id.settings_language_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.array_language,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		language.setAdapter(adapter);
		language.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		notifications = (CheckBox) findViewById(R.id.settings_notification_checkbox);
		notifications.setChecked(sendNotifications);
		notifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("notifications", isChecked);
				editor.commit();
			}
		});
		createDrawerList();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}
}
