package com.dnashot.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.dnashot.activities.InfoActivity;
import com.dnashot.activities.SettingsActivity;
import com.dnashot.activities.TakePictureActivity;
import com.dnashot.system.service.ResultProcessingManager;
import com.dnashot.v0.R;

@SuppressLint("InflateParams")
public class DrawerActivity extends Activity implements
		ListView.OnItemClickListener {
	public DrawerLayout drawerLayout;
	public FrameLayout activityContent;
	public ListView drawerList;
	
	protected static final int SELECT_PICTURE = 1;
	protected String selectedImagePath;	

	@Override
	public void setContentView(int layoutResID) {
		drawerLayout = (DrawerLayout) getLayoutInflater().inflate(
				R.layout.navigation_drawer_layout, null);
		activityContent = (FrameLayout) drawerLayout
				.findViewById(R.id.act_content);
		getLayoutInflater().inflate(layoutResID, activityContent, true);
		super.setContentView(drawerLayout);
	}

	public void createDrawerList() {
		String[] options = new String[] {
				getResources().getString(R.string.title_activity_take_picture),
				getResources()
						.getString(R.string.title_activity_upload_picture),
				getResources().getString(R.string.title_activity_info),
				getResources().getString(R.string.title_activity_settings) };
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(new DrawerAdapter(this, options));
		drawerList.setOnItemClickListener(this);
		
		drawerList.bringToFront();
		drawerLayout.requestLayout();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Intent it = null;
		switch (position) {
		case 0:
			it = new Intent(getApplicationContext(), TakePictureActivity.class);
			break;
		case 1:
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"),
					DrawerActivity.SELECT_PICTURE);			
			break;
		case 2:
			it = new Intent(getApplicationContext(), InfoActivity.class);
			break;
		case 3:
			it = new Intent(getApplicationContext(), SettingsActivity.class);
			break;
		}
		
		drawerLayout.closeDrawer(drawerList);
		
		if(it != null)
			startActivity(it);
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
							Bitmap bitmap = ResultManager.loadImage(selectedImagePath, 1);
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
							ResultProcessingManager.startProcessing(bitmap,getApplicationContext());
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
}
