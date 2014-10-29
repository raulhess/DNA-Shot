package com.dnareader.activities;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.dnareader.data.Hit;
import com.dnareader.data.Result;
import com.dnareader.processing.Blast;
import com.dnareader.system.DrawerActivity;
import com.dnareader.system.HitsAdapter;
import com.dnareader.v0.R;

public class ResultActivity extends DrawerActivity {
	private TextView time;
	private ExpandableListView content;
	private HitsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_result);
		
		Bundle bundle = getIntent().getExtras();

		time = (TextView) findViewById(R.id.result_time);
		content = (ExpandableListView) findViewById(R.id.expandable_list);

		if (bundle != null) {
			try {
				Result target = MainActivity.listResults.get(bundle.getInt("position"));
				setTitle(getResources().getString(R.string.result_id)
						+ target.getId());
				time.setText(getResources().getString(R.string.result_date) + target.getDate());
//				results.setText(bundle.getString("content"));
				List<Hit> hits = Blast.parseBlastXML(target.getBlastXML());
				adapter = new HitsAdapter(this, hits);
				content.setAdapter(adapter);
			} catch (Exception e) {
				Log.e("DNAReader", "Error: " + e.getMessage());
				e.printStackTrace();
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
