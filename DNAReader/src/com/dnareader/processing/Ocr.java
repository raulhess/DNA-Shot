package com.dnareader.processing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.googlecode.tesseract.android.TessBaseAPI.PageSegMode;


public class Ocr {
	
	
	
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/dnareader/";
	
	// You should have the trained data file in assets folder
	// You can get them at:
	// http://code.google.com/p/tesseract-ocr/downloads/list
	public static final String lang = "eng";

	private String TAG;	
	private Context context;
	
	public Ocr(Context context) {
		this.context = context;
		TAG = MainActivity.TAG;
		init();
	}
	

	private void init(){


		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		
		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = context.getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH
						+ "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();
				
				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}
		
	}
	
	public String doOcr(byte[] image){

			final long startTime = System.currentTimeMillis();			

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;

			Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length,options);
		
			// Convert to ARGB_8888, required by tess
			//bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);			

			// _image.setImageBitmap( bitmap );
			
			Log.v(TAG, "Before baseApi");

			TessBaseAPI baseApi = new TessBaseAPI();
			baseApi.setDebug(true);
			baseApi.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
			baseApi.setPageSegMode(PageSegMode.PSM_AUTO);
			baseApi.init(DATA_PATH, lang);
			baseApi.setImage(bitmap);
			
			String recognizedText = baseApi.getUTF8Text();
			
			baseApi.end();

			// You now have the text in recognizedText var, you can do anything with it.
			// We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
			// so that garbage doesn't make it to the display.

			Log.v(TAG, "OCRED TEXT: " + recognizedText);
			
			recognizedText = recognizedText.replaceAll("[^acgtACGT]", "");			

			final long endTime = System.currentTimeMillis();
			Log.v(TAG, "Tempo onPhotoTaken():" + (endTime-startTime)/1000.0);
			
			return recognizedText.toUpperCase();		

	}
	
}
