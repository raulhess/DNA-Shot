package com.dnareader.activities;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnareader.data.Result;
import com.dnareader.processing.Ocr;
import com.dnareader.system.DrawerActivity;
import com.dnareader.system.ResultManager;
import com.dnareader.system.ResultsAdapter;
import com.dnareader.v0.R;

@SuppressLint("InflateParams")
public class MainActivity extends DrawerActivity {
	public static final String TAG = "DNAReader";
	public static final String SETTINGS_FILE = "dna-reader-preferences";
	public static Typeface caviarDreams;

	private ActionBarDrawerToggle drawerToggle;

	private TextView takePicture;
	private TextView uploadPicture;

	private List<Result> listResults;
	private ListView results;
	private ResultsAdapter adapter;

	private Handler handler;
	Runnable checkResultsLoop = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "Checking results");
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				Ocr ocr  = new Ocr(getApplicationContext());
				try {
					listResults = ResultManager.loadResults(getApplicationContext());
					for (Result r : listResults) {
						switch (r.getState()) {
						case Result.UNPROCESSED:											
							
							
							r.setOcrText(ocr.doOcr(r.getImage()));
							r.setContent(r.getOcrText());
							r.setState(Result.DONE);
							
							break;
							
						case Result.OCR_PROCESSED:
						
							break;
						
						case Result.BLAST_PROCESSED:
							
							break;						

						default:
							break;
						}
					}
					ResultManager.saveResult(getApplicationContext(), listResults);

				} catch (Exception e) {
					Log.e(TAG, "Error checking results: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				Log.d(TAG, "Not connected to the internet");
			}
			updateResults();
			handler.postDelayed(this, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// creates the navigation drawer
		createDrawerList();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_navigation_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(getTitle());
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(getTitle());
				drawerList.bringToFront();
				drawerLayout.requestLayout();
				invalidateOptionsMenu();
			}

		};
		drawerLayout.setDrawerListener(drawerToggle);

		// instantiates the main menu views
		// lastResultsList = (ListView) findViewById(R.id.menu_last_results);
		takePicture = (TextView) findViewById(R.id.menu_take_picture);
		uploadPicture = (TextView) findViewById(R.id.menu_upload_picture);

		// changes the font of the main menu
		caviarDreams = Typeface.createFromAsset(getAssets(),
				"fonts/CaviarDreams.ttf");
		takePicture.setTypeface(caviarDreams);
		uploadPicture.setTypeface(caviarDreams);

		// creates the results list view
		results = (ListView) findViewById(R.id.menu_result_list);
		listResults = ResultManager.loadResults(this);
		adapter = new ResultsAdapter(this, listResults);
		results.setAdapter(adapter);
		results.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Result target = listResults.get(position);
				Log.d(TAG, "Clicked item with number: " + position);
				switch (target.getState()) {
				case Result.UNPROCESSED:
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(R.string.warning_not_sent),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.warning_waiting),
							Toast.LENGTH_SHORT).show();
					break;
				case Result.DONE:
					target.setChecked(true);
					ResultManager.saveResult(getApplicationContext(),
							listResults);
					Intent it = new Intent(getApplicationContext(),
							ResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("id", target.getId());
					bundle.putString("date", new Date().toString());
					bundle.putString("content", target.getContent());
					it.putExtras(bundle);
					startActivity(it);
					break;
				default:
					Log.d(MainActivity.TAG, "Unknown result state");
					break;
				}
			}

		});
		// notification test
		handler = new Handler();
		handler.postDelayed(checkResultsLoop, 1000);
		Log.d(TAG, ResultManager.loadResults(this).toString());
	}

	public void goTakePicture(View v) {
		Intent it = new Intent(this, TakePictureActivity.class);
		startActivity(it);
	}

	public void goUploadPicture(View v) {
		Intent it = new Intent(this, UploadPictureActivity.class);
		startActivity(it);
	}

	private void updateResults() {
		adapter.clear();
		listResults = ResultManager.loadResults(getApplicationContext());
		adapter.addAll(listResults);
		adapter.notifyDataSetChanged();
	}

	private class CheckResultsTask extends AsyncTask<URL, Integer, String> {	

		@Override
		protected String doInBackground(URL... url) {
			return null;
		}	
		

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
//		updateResults();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item);
	}
}
