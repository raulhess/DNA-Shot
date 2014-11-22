package com.dnashot.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnareader.v0.R;
import com.dnashot.data.Result;
import com.dnashot.system.DrawerActivity;
import com.dnashot.system.ResultManager;
import com.dnashot.system.ResultsAdapter;
import com.dnashot.system.service.ResultProcessingManager;

@SuppressLint("InflateParams")
public class MainActivity extends DrawerActivity {
	public static final String TAG = "DNAReader";
	public static final String SETTINGS_FILE = "dna-reader-preferences";
	public static final int NOTIFICATION_ID = 1;		
	private static final int SELECT_PICTURE = 1;
	public static final int RELOAD_GUI = 1;		
	
	private String selectedImagePath;	
	
	public static Typeface caviarDreams;
	private ActionBarDrawerToggle drawerToggle;

	private TextView takePicture;
	private TextView uploadPicture;

	public static List<Result> listResults;
	public static List<Result> receivedListResults;
	private ListView results;
	private ResultsAdapter adapter;

	public static Handler handler;
	public static Thread processingThread;

	private DeleteResultDialogFragment deleteFragment;

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
	
		listResults = new ArrayList<Result>();
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
							Toast.LENGTH_LONG).show();
					break;
				
				case Result.DONE:
					Intent it = new Intent(getApplicationContext(),
							ResultActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("id", target.getId());
					bundle.putInt("position", position);
					it.putExtras(bundle);
					startActivity(it);
					break;
				case Result.ERROR_OCR:
					Toast.makeText(
							getApplicationContext(),
							getResources()
									.getString(R.string.warning_ocr_error),
							Toast.LENGTH_LONG).show();
				default:
					Log.d(MainActivity.TAG, "Unknown result state");
					break;
				}
			}

		});
		
		final Activity caller = this;
		results.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Result target = listResults.get(position);
				deleteFragment = new DeleteResultDialogFragment(caller,
						target.getLongId(), position, false);
				deleteFragment.setCancelable(true);
				deleteFragment.show(getFragmentManager(), TAG);
				return false;
			}
		});
		
		if (handler == null)
			handler = new ThreadHandler();		
		
		listResults = receivedListResults;
		if(listResults == null){
			load();	
		}
		
		startThread();
		
	}

	public void goTakePicture(View v) {
		Intent it = new Intent(this, TakePictureActivity.class);
		startActivity(it);
	}

	public void goUploadPicture(View v) {
		// in onCreate or any event where your want the user to
		// select a file
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				Log.d("URI VAL",
						"selectedImageUri = " + selectedImageUri.toString());
				selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {
					// IF LOCAL IMAGE, NO MATTER IF ITS DIRECTLY FROM GALLERY
					// (EXCEPT PICASSA ALBUM),
					// OR OI/ASTRO FILE MANAGER. EVEN DROPBOX IS SUPPORTED BY
					// THIS BECAUSE DROPBOX DOWNLOAD THE IMAGE
					// IN THIS FORM -
					// file:///storage/emulated/0/Android/data/com.dropbox.android/...
					System.out.println("local image:" + selectedImagePath);							
					
					new Thread(new Runnable() {					
						public void run() {							
							Bitmap bitmap = ResultManager.loadImage(selectedImagePath, 1,false);
							ResultProcessingManager.startProcessing(bitmap,getApplicationContext());
						}
					}).start();
					
				} else {
					System.out.println("picasa/dropbox image!");
					loadPicasaImageFromGallery(selectedImageUri);
				}
			}
		}
	}
	
	private void loadPicasaImageFromGallery(final Uri uri) {
		String[] projection = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
			if (columnIndex != -1) {
				new Thread(new Runnable() {
					// NEW THREAD BECAUSE NETWORK REQUEST WILL BE MADE THAT WILL
					// BE A LONG PROCESS & BLOCK UI
					// IF CALLED IN UI THREAD
					public void run() {
						try {
							Bitmap bitmap = android.provider.MediaStore.Images.Media
									.getBitmap(getContentResolver(), uri);
							// THIS IS THE BITMAP IMAGE WE ARE LOOKING FOR.
							ResultProcessingManager.startProcessing(ResultManager.adjustImage(bitmap,1),getApplicationContext());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();
			}
		}
		cursor.close();
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			String filePath = cursor.getString(columnIndex);
			cursor.close();
			return filePath;
		} else
			return uri.getPath(); // FOR OI/ASTRO/Dropbox etc
	}

	private void load() {
		try {
			listResults = ResultManager.loadResults(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
			listResults = new ArrayList<Result>();
		}
	}

	public void startThread() {
		if ((processingThread == null) || (processingThread.getState() == Thread.State.TERMINATED)) {
			processingThread =  new Thread(new ResultProcessingManager());
			Log.d(TAG, "Starting processing thread");
			processingThread.start();
			handler.postDelayed(new Runnable() {				
				@Override
				public void run() {
					ResultProcessingManager.resumeProcessing(listResults, getApplicationContext());					
				}
			}, 2000);
			
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
		// load();
		updateGUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (processingThread != null)
			processingThread.interrupt();
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
					.setPositiveButton(R.string.confirm,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									ResultManager
											.clearResults(getApplicationContext());
									load();
									updateGUI();
									if (processingThread != null)
										processingThread.interrupt();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
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

	@SuppressLint("HandlerLeak")
	private class ThreadHandler extends Handler {

		// Create handleMessage function
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case RELOAD_GUI:
				updateGUI();
				break;
			}
		}

	}
}