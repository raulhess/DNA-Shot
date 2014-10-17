package com.dnareader.activities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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

	private List<Result> listResults = new ArrayList<Result>();
	private ListView results;
	private ResultsAdapter adapter;

	private List<CheckResultsTask> tasks = new ArrayList<MainActivity.CheckResultsTask>();
	private Handler handler;
	Runnable checkResultsLoop = new Runnable() {

		@Override
		public void run() {
			Log.d(TAG, "Checking results");
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				try {
					for (Result r : listResults) {
						if (r.getState() == 0 || r.getState() == 1) {
							// checks if there is already a task running for that id
							boolean taskRunning = false;
							for (CheckResultsTask t : tasks) {
								if (t.getId().equals(r.getId())) {
									taskRunning = true;
								}
							}
							if (!taskRunning) {
								CheckResultsTask newTask = new CheckResultsTask(
										getApplicationContext(), r.getId());
								newTask.execute(new URL(
										"http://md5.jsontest.com/?text="
												+ r.getId()));
								tasks.add(newTask);
								Log.d(TAG,
										"Sent search request for result with ID: "
												+ r.getId());
								r.setWaiting();
								ResultManager.saveResult(
										getApplicationContext(), listResults);
							}

						}
					}

				} catch (Exception e) {
					Log.e(TAG, "Error checking results: " + e.getMessage());
				}
			} else {
				Log.d(TAG, "Not connected to the internet");
			}
			Log.d(TAG,"Currently running tasks: " + tasks.size());
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
				switch (target.getState()) {
				case 0:
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
				case 2:
					target.setChecked(true);
					ResultManager.saveResult(getApplicationContext(),
							listResults);
					Intent it = new Intent(getApplicationContext(),
							ResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("id", target.getId());
					bundle.putByteArray("img", target.getImage());
					bundle.putString("date", new Date().toString());
					bundle.putString("content", target.getContent());
					it.putExtras(bundle);
					startActivity(it);
					updateResults();
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
	}

	public void goTakePicture(View v) {
		Intent it = new Intent(this, TakePictureActivity.class);
		startActivity(it);
	}

	public void goUploadPicture(View v) {
		Intent it = new Intent(this, UploadPictureActivity.class);
		startActivity(it);
	}

	@SuppressWarnings("unchecked")
	private void updateResults() {
		listResults.clear();
		try {
			InputStream is = openFileInput("results");
			InputStream buffer = new BufferedInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(buffer);
			List<Result> newList = (List<Result>) ois.readObject();
			listResults.addAll(newList);
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		adapter.notifyDataSetChanged();
	}

	private class CheckResultsTask extends AsyncTask<URL, Integer, String> {
		private Context context;
		private String resultId;

		public CheckResultsTask(Context context, String resultId) {
			this.context = context;
			this.resultId = resultId;
		}

		public String getId() {
			return this.resultId;
		}

		@Override
		protected String doInBackground(URL... url) {
			try {
				return readJsonFromUrl(url[0]);
			} catch (IOException e) {
				return "";
			}
		}

		private String readJsonFromUrl(URL url) throws IOException {
			BufferedReader reader = null;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);
			reader.close();
			return buffer.toString();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// finds the target result from this task
			List<Result> list = ResultManager.loadResults(context);
			Result target = null;
			for (Result r : list) {
				if (r.getId().equals(resultId)) {
					target = r;
					break;
				}
			}
			// checks the result in case a result was found
			if (target != null) {
				// possible broken URL
				if (result.length() == 0) {
					target.setContent("");
				} else {
					try {
						target.setDone();
						String strNotification = context.getResources()
								.getString(R.string.notify_done_A)
								+ target.getId()
								+ " "
								+ context.getResources().getString(
										R.string.notify_done_B);
						Toast.makeText(context, strNotification,
								Toast.LENGTH_SHORT).show();
						target.setContent(result);
						JSONObject json = new JSONObject(result);
						Log.d(MainActivity.TAG, json.toString());
					} catch (JSONException e) {
						Log.e(MainActivity.TAG, "Failed to parse JSON object: "
								+ e.getMessage());
					}
					ResultManager.saveResult(context, list);
					updateResults();
				}
			} else {
				// doesn't do anything in case a result couldn't be found
				Log.e(MainActivity.TAG,
						"A task was resolved but there was no result for the task's id ["
								+ resultId + "]");
			}
			tasks.remove(this);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		updateResults();
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
