package com.dnashot.system.service;

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

import com.dnareader.v0.R;
import com.dnashot.activities.MainActivity;
import com.dnashot.activities.ResultActivity;
import com.dnashot.data.Result;
import com.dnashot.processing.Blast;
import com.dnashot.processing.Ocr;
import com.dnashot.processing.PreProcessing;
import com.dnashot.system.ResultManager;
import com.googlecode.leptonica.android.Pix;

public class LoopThread implements Runnable {

	Context context;
	Result result;
	
	Ocr ocr;
	Blast blast;

	public LoopThread(Context context, Result result) {
		this.context = context;
		this.result = result;
	}

	@Override
	public void run() {
		Log.d(MainActivity.TAG, "Checking result");
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		Handler handler = MainActivity.handler;
		handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
		
		boolean done = true;	
		
		do {
			done = true;			
			try {
				
					int position = MainActivity.listResults.indexOf(result);
					if (result.getState() != Result.DONE
							&& result.getState() != Result.ERROR) {
						done = false;
					}

					Log.d(MainActivity.TAG, "Trying: " + result.getId() + " State:"
							+ result.getState());

					switch (result.getState()) {

					case Result.UNPROCESSED:
					case Result.PREPROCESSING_STARTED:

						result.setState(Result.PREPROCESSING_STARTED);
						handler.sendEmptyMessage(MainActivity.RELOAD_GUI);

						PreProcessing p = new PreProcessing();
						Bitmap fullImage = null;
						try {
							fullImage = ResultManager.loadFullImage(context,
									result.getLongId(), 1);
							Pix threshold = p
									.adaptativeThreshold(bitmapToByteArray(fullImage));
							byte[] deskew = p.deskew(threshold);
							result.setPreProcessedimage(BitmapFactory
									.decodeByteArray(deskew, 0, deskew.length));

							result.setState(Result.PREPROCESSING_FINISHED);
							ResultManager.updateResultState(context, result);
							ResultManager.savePreImage(context, result.getLongId(),
									result.getPreProcessedimage());
							handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						break;

					case Result.PREPROCESSING_FINISHED:
					case Result.OCR_STARTED:

						if (ocr == null)
							ocr = new Ocr(context);

						result.setState(Result.OCR_STARTED);
						handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
						Bitmap preImage = null;
						try {
							preImage = ResultManager.loadPreImage(context,
									result.getLongId(), 1);
							String text = ocr
									.doOcr(bitmapToByteArray(preImage));
							if (text.length() > 20) {
								result.setOcrText(text);
								result.setState(Result.OCR_FINISHED);
								ResultManager.updateResult(context, result);
								Log.d(MainActivity.TAG, "OCR Processed");
							} else {
								result.setState(Result.ERROR_OCR);
							}

							ResultManager.updateResultState(context, result);
							handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
						}
						break;

					case Result.OCR_FINISHED:

						if (blast == null)
							blast = new Blast();

						if (networkInfo != null && networkInfo.isConnected()) {

							String rid = blast.startBlast(result.getOcrText());
							result.setRid(rid);
							result.setState(Result.BLAST_STARTED);
							Log.d(MainActivity.TAG, "Blast request sent");

							ResultManager.updateResult(context, result);
							ResultManager.updateResultState(context, result);
							handler.sendEmptyMessage(MainActivity.RELOAD_GUI);

						} else {
							Log.d(MainActivity.TAG,
									"Not connected to the internet");
							Thread.sleep(10000);
						}

						break;

					case Result.BLAST_STARTED:

						if (blast == null)
							blast = new Blast();

						if (networkInfo != null && networkInfo.isConnected()) {
							Log.d(MainActivity.TAG, "Checking Blast request");
							Thread.sleep(5000);
							try {
								String xml = blast.checkBlast(result.getRid());
								if (xml != null) {
									Log.d(MainActivity.TAG, xml);
									result.setBlastXML(xml);
									result.setHits(Blast.parseBlastXML(xml));
									result.setState(Result.DONE);
									SharedPreferences settings = context
											.getSharedPreferences(
													MainActivity.SETTINGS_FILE,
													0);
									if (settings.getBoolean("notifications",
											false)) {
										sendNotification(result, context, position);
									}
									Log.d(MainActivity.TAG,
											"Blast XML received");
									ResultManager.updateResultState(context, result);
									ResultManager.addHits(context, result);
								}
							} catch (Exception e) {
								Log.d(MainActivity.TAG,
										"Request id non existent, trying again");
								result.setState(Result.OCR_FINISHED);
								ResultManager.updateResultState(context, result);
							}

							handler.sendEmptyMessage(MainActivity.RELOAD_GUI);

						} else {
							Log.d(MainActivity.TAG,
									"Not connected to the internet");
							Thread.sleep(10000);
						}

						break;

					default:
						break;
					}
				

			} catch (Exception e) {
				Log.e(MainActivity.TAG,
						"Error checking results: " + e.getMessage());
				e.printStackTrace();
				handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			if (done) {
				Log.d(MainActivity.TAG, "Done processing.");
			}

		} while (!done && !MainActivity.listResults.isEmpty());

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
