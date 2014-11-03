package com.dnareader.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnareader.data.Result;
import com.dnareader.processing.Blast;
import com.dnareader.processing.Ocr;
import com.dnareader.system.DrawerActivity;
import com.dnareader.system.ResultManager;
import com.dnareader.system.ResultsAdapter;
import com.dnareader.system.service.LoopThread;
import com.dnareader.v0.R;

@SuppressLint("InflateParams")
public class MainActivity extends DrawerActivity {
	public static final String TAG = "DNAReader";
	public static final String SETTINGS_FILE = "dna-reader-preferences";
	public static Typeface caviarDreams;

	private ActionBarDrawerToggle drawerToggle;

	private TextView takePicture;
	private TextView uploadPicture;

	public static List<Result> listResults;
	private ListView results;
	private ResultsAdapter adapter;
	
	public static Ocr ocr;
	public static Blast blast;

	public static Handler handler;
	static Runnable checkResultsLoop;
	private static Thread thread;

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
		if(listResults == null)
			load();
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
					ResultManager.saveResult(getApplicationContext());
					Intent it = new Intent(getApplicationContext(),
							ResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("id", target.getId());
					bundle.putInt("position", position);
					it.putExtras(bundle);
					startActivity(it);
					break;
				default:
					Log.d(MainActivity.TAG, "Unknown result state");
					break;
				}
			}

		});
		
		if (checkResultsLoop == null)
			checkResultsLoop = new LoopThread(getApplicationContext());				
		if (ocr ==null)		
			ocr = new Ocr(getApplicationContext());
		if (blast == null)
			blast = new Blast();		
		if (handler == null)
			handler = new ThreadHandler();		
	
		startThread();		
		
	}
	
	
	public void goTakePicture(View v) {
		Intent it = new Intent(this, TakePictureActivity.class);
		startActivity(it);
	}

	public void goUploadPicture(View v) {
		Intent it = new Intent(this, UploadPictureActivity.class);
		startActivity(it);
	}
	
	private void load(){
		try{
			ResultManager.loadResults(getApplicationContext());
		}catch(Exception e){
			listResults = new ArrayList<Result>();
		}
	}
	
	private void save(){
		ResultManager.saveResult(getApplicationContext());
	}
	
	public static void startThread(){	
		if ((thread == null) || (thread.getState() == Thread.State.TERMINATED)) {		
			thread = new Thread(checkResultsLoop);
			handler.postDelayed(new Runnable() {
				public void run() {
					Log.d(TAG, "Restarting thread");					
					thread.start();
				}
			}, 5000);
		}
	}

	private void updateGUI() {
		Log.d(TAG, "Updating GUI");	
		adapter.clear();		
		adapter.addAll(listResults);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
		updateGUI();
	}
	
	@Override
	protected void onDestroy() {		
		super.onDestroy();
		if (thread != null)
			thread.interrupt();
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
		switch (item.getItemId()) {
		case R.id.action_clear_results:
			DialogFragment clearResults = new ClearResultsDialogFragment();
			clearResults.setCancelable(true);
			clearResults.show(getFragmentManager(), TAG);
			return true;
		default:
			return drawerToggle.onOptionsItemSelected(item);
		}
	}
	
	public class ClearResultsDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(R.string.clear_results_title)
	               .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   ResultManager.clearResults(getApplicationContext());
	                	   load();
	                	   updateGUI();
	                	   if (thread != null)
	               				thread.interrupt();
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	private class ThreadHandler extends Handler{
		

		// Create handleMessage function
		public void handleMessage(Message msg) {	
			
			switch (msg.what) {		
			
			case LoopThread.RELOAD_GUI:
				updateGUI();
				break;
				
			case LoopThread.SAVE:
				save();
				break;		
			}

			updateGUI();
		}

	
		
	}
}
