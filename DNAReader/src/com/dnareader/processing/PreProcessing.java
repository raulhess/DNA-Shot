package com.dnareader.processing;


import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;

public class PreProcessing {
	
	public Pix adaptativeThreshold(byte[] original){		

		Bitmap bmp=BitmapFactory.decodeByteArray(original,0,original.length);
		
		Pix pix = ReadFile.readBitmap(bmp);
		pix = Convert.convertTo8(pix);
		pix = Binarize.sauvolaBinarizeTiled(pix,14,0.1f,1,1);
		//Bitmap bmp2 = WriteFile.writeBitmap(pix);		
		
		//ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
		//byte[] byteArray = stream.toByteArray();
		
		//return byteArray;
		return pix;
	}
	
	public byte[] deskew(Pix original){
		
		float angle = Skew.findSkew(original);
		
		Log.d(MainActivity.TAG, "Deskew angle: " + angle);
		
		Pix rotated = Rotate.rotate(original, angle);		
		
		Bitmap bmp2 = WriteFile.writeBitmap(rotated);		
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		return byteArray;		
		
	}

}
