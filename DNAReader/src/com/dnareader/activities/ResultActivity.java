package com.dnareader.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dnareader.system.DrawerActivity;
import com.dnareader.v0.R;

public class ResultActivity extends DrawerActivity {
	private TextView time;
	private TextView results;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_result);
		
		Bundle bundle = getIntent().getExtras();

		time = (TextView) findViewById(R.id.result_time);
		results = (TextView) findViewById(R.id.result_text);

		if (bundle != null) {
			try {
				setTitle(getResources().getString(R.string.result_id)
						+ bundle.getString("id"));
				time.setText(getResources().getString(R.string.result_date) + bundle.getString("date"));
				results.setText(bundle.getString("content"));
			} catch (Exception e) {
				Log.e("DNAReader", "Error: " + e.getMessage());
				this.finish();
			}
		}
		
		createDrawerList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
