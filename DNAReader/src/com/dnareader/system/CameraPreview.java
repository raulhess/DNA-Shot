package com.dnareader.system;

import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;

	public CameraPreview(Context context, Camera camera) {
		super(context);
		mCamera = camera;

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		// mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(holder);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				mCamera.setDisplayOrientation(90);
			}
			mCamera.startPreview();
			Log.d("DNAReader", "Camera Started");
		} catch (IOException e) {
			Log.d("DNAReader", "Camera couldn't be started: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
			Log.d("DNAReader", "Camera Stopped");
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			Log.d("DNAReader", "Camera Started");
		} catch (Exception e) {
			Log.d("DNAReader", "Camera couldn't be started: " + e.getMessage());
		}
	}

	public void startPreview() {
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			Log.d("DNAReader", "Camera Started");
		} catch (Exception e) {
			Log.d("DNAReader", "Camera couldn't be started: " + e.getMessage());
		}
	}

	public void releaseCamera() {
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
			Log.d("DNAReader", "Camera Stopped");
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
	}

	public void restartPreview() {
		Log.d("DNAReader", "Restarting camera preview");
		try {
			mCamera.stopPreview();
			Log.d("DNAReader", "Camera Stopped");
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
			Log.d("DNAReader", "Camera Started");
		} catch (Exception e) {
			Log.d("DNAReader", "Camera couldn't be started: " + e.getMessage());
		}
	}

	public Camera getmCamera() {
		return mCamera;
	}

	public void setmCamera(Camera mCamera) {
		this.mCamera = mCamera;
	}
	
	
}