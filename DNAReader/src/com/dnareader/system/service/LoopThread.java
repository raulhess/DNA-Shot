package com.dnareader.system.service;

import java.lang.reflect.WildcardType;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;

public class LoopThread implements Runnable {
	
	public static final int SAVE = 0;	
	public static final int RESTART = 1;	
	public static final int RELOAD_GUI = 2;	

	Context context;

	public LoopThread(Context context) {
		this.context = context;
	}

	@Override
	public void run() {
		Log.d(MainActivity.TAG, "Checking results");
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		Handler handler = MainActivity.handler;
		
		boolean done = true;	
		
		do {	
			try {				
				for (Result r : MainActivity.listResults) {
					if (r.getState() != Result.DONE && r.getState() != Result.ERROR){
						done = false;
					}
					switch (r.getState()) {
					case Result.UNPROCESSED:

						r.setState(Result.OCR_STARTED);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						String text = MainActivity.ocr.doOcr(r.getImage());
						if (text.length() > 10) {
							r.setOcrText(text);
							r.setState(Result.OCR_FINISHED);
							Log.d(MainActivity.TAG, "OCR Processed");
						} else {
							r.setState(Result.ERROR);
						}
						
						
						handler.sendEmptyMessage(SAVE);
						handler.sendEmptyMessage(RELOAD_GUI);

						break;

					case Result.OCR_FINISHED:
						
						if (networkInfo != null && networkInfo.isConnected()) {
						
						String rid = MainActivity.blast.startBlast(r.getOcrText());						
						r.setRid(rid);
						r.setState(Result.BLAST_STARTED);
						Log.d(MainActivity.TAG, "Blast request sent");
						
											
						handler.sendEmptyMessage(SAVE);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						} else {
							Log.d(MainActivity.TAG, "Not connected to the internet");
							Thread.sleep(10000);
						}

						break;

					case Result.BLAST_STARTED:
						
						if (networkInfo != null && networkInfo.isConnected()) {
							Log.d(MainActivity.TAG, "Checking Blast request");
							Thread.sleep(5000);
						String xml = MainActivity.blast.checkBlast(r.getRid());
						
						if (xml != null) {
							Log.d(MainActivity.TAG, xml);
							r.setBlastXML(xml);
							r.setState(Result.DONE);
							Log.d(MainActivity.TAG, "Blast XML received");
						} 
						
						
						handler.sendEmptyMessage(SAVE);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						} else {
							Log.d(MainActivity.TAG, "Not connected to the internet");
							Thread.sleep(10000);
						}

						break;

					default:						
						break;
					}
				}				

			} catch (Exception e) {				
				Log.e(MainActivity.TAG,	"Error checking results: " + e.getMessage());
				e.printStackTrace();
			}
		
		if (done){
			Log.d(MainActivity.TAG, "Done processing.");
		}
		
		}while(!done);
		
	}

}
