package com.dnareader.system.service;

import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
		
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				List<Result> listResults;
				listResults = MainActivity.listResults;
				for (Result r : listResults) {
					switch (r.getState()) {
					case Result.UNPROCESSED:

						// r.setState(Result.OCR_PROCESSING);
						// handler.sendEmptyMessage(1);
						String text = MainActivity.ocr.doOcr(r.getImage());
						if (text.length() > 5) {
							r.setOcrText(text);
							r.setState(Result.OCR_PROCESSED);
							Log.d(MainActivity.TAG, "OCR Processed");
						} else {
							r.setState(Result.ERROR);
						}
						
						//MainActivity.listResults = listResults;
						MainActivity.handler.sendEmptyMessage(SAVE);
						MainActivity.handler.sendEmptyMessage(RELOAD_GUI);

						break;

					case Result.OCR_PROCESSED:

						// r.setState(Result.BLAST_PROCESSING);
						// handler.sendEmptyMessage(1);
						
						String rid = MainActivity.blast.startBlast(r.getOcrText());						
						r.setRid(rid);
						r.setState(Result.BLAST_PROCESSING);
						Log.d(MainActivity.TAG, "Blast request sent");
						
						//MainActivity.listResults = listResults;						
						MainActivity.handler.sendEmptyMessage(SAVE);
						MainActivity.handler.sendEmptyMessage(RELOAD_GUI);

						break;

					case Result.BLAST_PROCESSING:
						
						String xml = MainActivity.blast.checkBlast(r.getRid());
						Log.d(MainActivity.TAG, "Checking Blast request");
						if (xml != null) {
							Log.d(MainActivity.TAG, xml);
							r.setBlastXML(xml);
							r.setState(Result.DONE);
							Log.d(MainActivity.TAG, "Blast XML received");
						} 
						
						//MainActivity.listResults = listResults;
						MainActivity.handler.sendEmptyMessage(SAVE);
						MainActivity.handler.sendEmptyMessage(RELOAD_GUI);

						break;

					default:						
						break;
					}
				}
				

			} catch (Exception e) {				
				Log.e(MainActivity.TAG,	"Error checking results: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Log.d(MainActivity.TAG, "Not connected to the internet");
		}
		
		MainActivity.handler.sendEmptyMessage(RESTART);
		
	}

}
