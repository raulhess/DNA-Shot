package com.dnashot.activities;

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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.dnashot.system.DrawerActivity;
import com.dnashot.v0.R;

public class SettingsActivity extends DrawerActivity {
	private TextView notificationTitle;
	private CheckBox notifications;
	private TextView languageTitle;
	private Spinner language;
	private TextView compressionTitle;
	private SeekBar compression;
	private TextView compressionHelp;
	private SharedPreferences settings;

	private boolean sendNotifications;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		settings = getSharedPreferences(MainActivity.SETTINGS_FILE, 0);
		notificationTitle = (TextView) findViewById(R.id.notification_title);
		notificationTitle.setText("Notifications");
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

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		languageTitle = (TextView) findViewById(R.id.language_title);
		languageTitle.setText("Language");
		
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

		compressionTitle = (TextView) findViewById(R.id.compression_title);
		compressionTitle.setText(getResources().getString(
				R.string.settings_image_compression));
		createDrawerList();
		compressionHelp = (TextView) findViewById(R.id.compression_help);
		compression = (SeekBar) findViewById(R.id.compression_bar);
		compression.setMax(4);
		compression.setProgress(settings.getInt("compression", 2));
		changeCompressionHint(settings.getInt("compression", 2));
		compression.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("compression", progress);
				editor.commit();
				changeCompressionHint(progress);
			}
		});
	}

	private void changeCompressionHint(int value) {
		switch (value) {
		case 0:
			compressionHelp.setText(getResources().getString(R.string.settings_really_high_comp));
			break;
		case 1:
			compressionHelp.setText(getResources().getString(R.string.settings_high_comp));
			break;
		case 2:
			compressionHelp.setText(getResources().getString(R.string.settings_normal_comp));
			break;
		case 3:
			compressionHelp.setText(getResources().getString(R.string.settings_low_comp));
			break;
		case 4:
			compressionHelp.setText(getResources().getString(R.string.settings_really_low_comp));
			break;
		default:
			compressionHelp.setText("Invalid Compression Value");
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}
}
