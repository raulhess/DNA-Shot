package com.dnareader.system.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;
import com.dnareader.system.ResultManager;

public class ResultProcessingManager {
	
	private Context context;

	public ResultProcessingManager(Context context) {
		this.context = context;
	}
	
	public void startProcessing(byte[] imageData){		
		Bitmap bmp = BitmapFactory
				.decodeByteArray(imageData, 0, imageData.length);
		startProcessing(bmp);
	}
	
	public void startProcessing(Bitmap bitmap){
		Result r = new Result();
		r.setState(Result.UNPROCESSED);		
		r.setThumbnail(ResultManager.getThumbnail(bitmap));
		r.setImage(bitmap);
		long id = ResultManager.addResult(context, r);
		r.setId(id);
		
		MainActivity.listResults.add(0, r);
		MainActivity.startThread();
	}

}
