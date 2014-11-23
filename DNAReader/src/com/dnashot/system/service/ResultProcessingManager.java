package com.dnashot.system.service;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dnashot.activities.MainActivity;
import com.dnashot.data.Result;
import com.dnashot.system.ResultManager;

public class ResultProcessingManager implements Runnable{
	
	
	public static Handler handler;

	public static void startProcessing(byte[] imageData,  Context context){		
		Bitmap bmp = BitmapFactory
				.decodeByteArray(imageData, 0, imageData.length);
		startProcessing(bmp,context);
	}
	
	public static void startProcessing(Bitmap bitmap, Context context){
		Log.d(MainActivity.TAG, "Starting an image");
		Result r = new Result();
		r.setState(Result.UNPROCESSED);		
		long id = ResultManager.addResult(context, r, bitmap);
		r.setId(id);
		
		MainActivity.listResults.add(0, r);		
		MainActivity.handler.sendEmptyMessage(MainActivity.RELOAD_GUI);
		MainActivity.handler.sendEmptyMessage(MainActivity.RELOAD_THREAD);
		handler.post(new LoopThread(context, r));
	}
	
	public static void resumeProcessing(List<Result> resultList, Context context){
		Log.d(MainActivity.TAG, "Resuming incomplete results");
		for (Result result : resultList) {
			if (result.getState() != Result.DONE && result.getState() != Result.ERROR && result.getState() != Result.ERROR_OCR)
				handler.post(new LoopThread(context, result));
		}	
		
	}

	@Override
	public void run() {
		try {
		    // preparing a looper on current thread     
		    // the current thread is being detected implicitly
		    Looper.prepare();
		 
		    // now, the handler will automatically bind to the
		    // Looper that is attached to the current thread
		    // You don't need to specify the Looper explicitly
		    handler = new Handler();
		     
		    // After the following line the thread will start
		    // running the message loop and will not normally
		    // exit the loop unless a problem happens or you
		    // quit() the looper (see below)
		    Looper.loop();
		  } catch (Throwable t) {
		    Log.e(MainActivity.TAG, "halted due to an error", t);
		  } 
		
	}

}
