package com.dnareader.system.service;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;
import com.dnareader.processing.Blast;
import com.dnareader.processing.PreProcessing;
import com.dnareader.system.ResultManager;
import com.googlecode.leptonica.android.Pix;

public class LoopThread implements Runnable {
		
	public static final int RELOAD_GUI = 1;		

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
		handler.sendEmptyMessage(RELOAD_GUI);
		
		boolean done = true;	
		
		do {	
			done = true;
			Log.d(MainActivity.TAG, "Looping...");
			try {				
				for (Result r : MainActivity.listResults) {
					if (r.getState() != Result.DONE && r.getState() != Result.ERROR){
						done = false;
					}
					
					Log.d(MainActivity.TAG, "Trying: " + r.getId() + " State:" + r.getState());
					
					switch (r.getState()) {
					
					case Result.UNPROCESSED:
					case Result.PREPROCESSING_STARTED:
						
						r.setState(Result.PREPROCESSING_STARTED);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						PreProcessing p = new PreProcessing();						
						Pix threshold = p.adaptativeThreshold(bitmapToByteArray(ResultManager.loadImage(context, ResultManager.FILEPREFIX + r.getLongId() + ResultManager.IMG)));						
						byte[] deskew = p.deskew(threshold);						
						r.setPreProcessedimage(BitmapFactory.decodeByteArray(deskew, 0, deskew.length));								
						
						r.setState(Result.PREPROCESSING_FINISHED);
						ResultManager.updateResultState(context, r);
						ResultManager.saveImage(context, ResultManager.FILEPREFIX + r.getLongId() + ResultManager.PREPROCESSED_IMG, r.getPreProcessedimage());
						handler.sendEmptyMessage(RELOAD_GUI);						
						
						break;
					
					case Result.PREPROCESSING_FINISHED:
					case Result.OCR_STARTED:

						r.setState(Result.OCR_STARTED);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						String text = MainActivity.ocr.doOcr(bitmapToByteArray(ResultManager.loadImage(context, ResultManager.FILEPREFIX + r.getLongId() + ResultManager.PREPROCESSED_IMG)));
						if (text.length() > 20) {
							r.setOcrText(text);
							r.setState(Result.OCR_FINISHED);
							ResultManager.updateResult(context, r);
							Log.d(MainActivity.TAG, "OCR Processed");
						} else {
							r.setState(Result.ERROR);
						}
						
						
						ResultManager.updateResultState(context, r);
						handler.sendEmptyMessage(RELOAD_GUI);

						break;

					case Result.OCR_FINISHED:
						
						if (networkInfo != null && networkInfo.isConnected()) {
						
						String rid = MainActivity.blast.startBlast(r.getOcrText());						
						r.setRid(rid);
						r.setState(Result.BLAST_STARTED);
						Log.d(MainActivity.TAG, "Blast request sent");
						
											
						ResultManager.updateResultState(context, r);
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
							r.setHits(Blast.parseBlastXML(xml));
							r.setState(Result.DONE);
							Log.d(MainActivity.TAG, "Blast XML received");
							ResultManager.updateResultState(context, r);
							ResultManager.addHits(context, r);
						} 												
						
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
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {					
					e1.printStackTrace();
				}
			}
		
		if (done){
			Log.d(MainActivity.TAG, "Done processing.");
		}
		
		}while(!done && !MainActivity.listResults.isEmpty());
		
	}
	
	public static byte[] bitmapToByteArray(Bitmap bmp){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

}
