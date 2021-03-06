package com.dnashot.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.dnashot.system.CameraPreview;
import com.dnashot.system.DrawerActivity;
import com.dnashot.system.service.ResultProcessingManager;
import com.dnashot.v0.R;

public class TakePictureActivity extends DrawerActivity {
	private FrameLayout mainFrameLayout;
	private Camera camera;
	private CameraPreview cameraPreview;
	private ProgressDialog mProgressDialogue;
	private Button buttonTakePicture;
	private PictureCallback picture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				ResultProcessingManager.startProcessing(data,getApplicationContext());
				pictureTaken();				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);			
		setContentView(R.layout.activity_take_picture);
		Typeface caviarDreams = Typeface.createFromAsset(getAssets(),
				"fonts/CaviarDreams.ttf");
		buttonTakePicture = (Button) findViewById(R.id.button_take_picture);
		buttonTakePicture.setTypeface(caviarDreams);
		createDrawerList();
	}
	
	private void startCamera(){
		
		mainFrameLayout = (FrameLayout) findViewById(R.id.main_frame_layout);
		if (!checkCameraHardware(this)) {
			TextView content = new TextView(getApplicationContext());
			content.setLayoutParams(new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			content.setPadding(20, 20, 20, 20);
			content.setText("Your device does not have a camera.");
			content.setTextColor(getResources().getColor(android.R.color.white));
			mainFrameLayout.addView(content);
		} else {
			camera = getCameraInstance();
			if (camera != null) {
				cameraPreview = new CameraPreview(this, camera);
				mainFrameLayout.addView(cameraPreview);
			}
		}
		
	}

	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
			Camera.Parameters params = c.getParameters();
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			c.setParameters(params);
		} catch (Exception e) {
			Log.d(MainActivity.TAG, "Camera not opened : " + e.getMessage());
		}
		return c; // returns null if camera is unavailable
	}

	public void takePicture(View v) {
		mProgressDialogue = ProgressDialog.show(this, "Aguarde",
				"Processando Foto", true);
		buttonTakePicture.setEnabled(false);
		camera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				camera.takePicture(null, null, picture);
			}
		});		
	}

	public void pictureTaken() {
		mProgressDialogue.dismiss();
		this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraPreview.releaseCamera();
		mainFrameLayout.removeView(cameraPreview);
	}
		
	@Override
	protected void onResume() {		
		super.onResume();	
		startCamera();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		NavUtils.navigateUpFromSameTask(this);
	}
}
