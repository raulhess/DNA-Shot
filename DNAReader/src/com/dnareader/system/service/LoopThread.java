package com.dnareader.system.service;

import java.io.ByteArrayOutputStream;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.activities.ResultActivity;
import com.dnareader.data.Result;
import com.dnareader.processing.Blast;
import com.dnareader.processing.PreProcessing;
import com.dnareader.system.ResultManager;
import com.dnareader.v0.R;
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
					int position = MainActivity.listResults.indexOf(r);
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
						Pix threshold = p.adaptativeThreshold(bitmapToByteArray(ResultManager.loadFullImage(context, r.getLongId(), 1)));						
						byte[] deskew = p.deskew(threshold);						
						r.setPreProcessedimage(BitmapFactory.decodeByteArray(deskew, 0, deskew.length));								
						
						r.setState(Result.PREPROCESSING_FINISHED);
						ResultManager.updateResultState(context, r);
						ResultManager.savePreImage(context, r.getLongId(), r.getPreProcessedimage());
						handler.sendEmptyMessage(RELOAD_GUI);						
						
						break;
					
					case Result.PREPROCESSING_FINISHED:
					case Result.OCR_STARTED:

						r.setState(Result.OCR_STARTED);
						handler.sendEmptyMessage(RELOAD_GUI);
						
						String text = MainActivity.ocr.doOcr(bitmapToByteArray(ResultManager.loadPreImage(context, r.getLongId(), 1)));
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
							SharedPreferences settings = context.getSharedPreferences(MainActivity.SETTINGS_FILE, 0);
							if(settings.getBoolean("notifications", false)){
								sendNotification(r, context, position);
							}
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
	
	private static byte[] bitmapToByteArray(Bitmap bmp){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}
	
	private static void sendNotification(Result r, Context context, int position){
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Result Ready")
		        .setContentText("You result with id = " + r.getLongId() + " is ready")
		        .setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, ResultActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("id", r.getId());
		bundle.putInt("position", position);
		resultIntent.putExtras(bundle);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ResultActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
	}

}
