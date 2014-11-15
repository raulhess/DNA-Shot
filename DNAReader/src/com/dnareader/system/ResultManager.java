package com.dnareader.system;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Hit;
import com.dnareader.data.Hsp;
import com.dnareader.data.Result;

public class ResultManager {
	public static final String FILENAME = "results";
	public static final String DIRECTORY = "/DNAShot";
	
	private static Cursor resultsCursor;
	private static Cursor hitsCursor;
	private static Cursor hspsCursor;
	
	public static void addResult(Context context, Result r){
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		long id = db.insertResult(r);
		db.close();
		saveImage(context, id, r.getImage());
		saveImagePreProcessed(context, id, r.getPreProcessedimage());
	}
	
	public static void addHits(Context context, Result r){
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		for(Hit hit : r.getHits()){
			long hitId = db.insertHit(hit,r.getLongId());
			Log.d(MainActivity.TAG, "Saved 'hit' with id = " + hitId);
			for(Hsp hsp : hit.getHsps()){
				long hspId = db.insertHsp(hsp,hitId);
				Log.d(MainActivity.TAG, "Saved 'hsp' with id = " + hspId);
			}
		}
		db.close();
	}

	public static void clearResults(Context context) {
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		db.deleteAll();
		db.close();
		
	}
	
	public static void setChecked(Context context, long id){
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		boolean checked = db.updateResultChecked(id);
		db.close();
		if(checked)
			Log.d(MainActivity.TAG, "Set checked result with id = " + id);
		else
			Log.d(MainActivity.TAG, "Couldn't set checked result with id = " + id);
	}
	
	public static void updateResultState(Context context, Result r){
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		boolean updated = db.updateResultState(r.getLongId(), r.getState());
		db.close();
		if(updated)
			Log.d(MainActivity.TAG, "Updated result state with id = " + r.getLongId());
		else
			Log.d(MainActivity.TAG, "Couldn't update result state with id = " + r.getLongId());
	}
	
	public static void updateResult(Context context, Result r){
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		boolean updated = db.updateResult(r);
		db.close();
		if(updated)
			Log.d(MainActivity.TAG, "Updated result with id = " + r.getLongId());
		else
			Log.d(MainActivity.TAG, "Couldn't update result with id = " + r.getLongId());
	}

	public static List<Result> loadResults(Context context){
		List<Result> results = new ArrayList<Result>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		resultsCursor = db.loadResults();
		if(resultsCursor.moveToFirst()){
			do {
				Result r = new Result();
				r.setId(resultsCursor.getLong(resultsCursor.getColumnIndex(ResultDatabase.KEY_ID)));
				Log.d(MainActivity.TAG, "Loading result with id = " + r.getId());
				r.setState(resultsCursor.getInt(resultsCursor.getColumnIndex(ResultDatabase.KEY_STATE)));
				r.setOcrText(resultsCursor.getString(resultsCursor.getColumnIndex(ResultDatabase.KEY_OCR)));
				r.setBlastXML(resultsCursor.getString(resultsCursor.getColumnIndex(ResultDatabase.KEY_XML)));
				r.setImage(loadImageBytes(context, r.getLongId()));
				r.setPreProcessedimage(loadImageBytesPreProcessed(context, r.getLongId()));
				r.setThumbnail(getThumbnail(r.getImage()));
				r.setHits(loadHits(context, r.getLongId()));
				
				results.add(0,r);
			}while(resultsCursor.moveToNext());
		}
		db.close();
		return results;
	}
	
	public static List<Hit> loadHits(Context context, long id){
		List<Hit> hits = new ArrayList<Hit>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		hitsCursor = db.loadHits(id);
		Log.d(MainActivity.TAG, "Loading hits from result with id = " + id);
		Log.d(MainActivity.TAG, "Found: " + hitsCursor.getCount());
		if(hitsCursor.moveToFirst()){
			do {
				Hit h = new Hit();
				h.setHit_id(hitsCursor.getLong(hitsCursor.getColumnIndex(ResultDatabase.KEY_HIT_ID)));
				h.setHit_len(hitsCursor.getString(hitsCursor.getColumnIndex(ResultDatabase.KEY_LEN)));
				h.setHit_def(hitsCursor.getString(hitsCursor.getColumnIndex(ResultDatabase.KEY_HITNAME)));
				h.setResultId(id);
				h.setHsps(loadHsps(context, h.getLongHit_id()));
				hits.add(h);
			}while(hitsCursor.moveToNext());
		}
		db.close();
		return hits;
	}
	
	public static List<Hsp> loadHsps(Context context, long id){
		List<Hsp> hsps = new ArrayList<Hsp>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		hspsCursor = db.loadHsps(id);
		if(hspsCursor.moveToFirst()){
			do {
				Hsp h = new Hsp();
				h.setHitId(id);
				h.setHsp_align_len(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_ALIGN_LEN)));
				h.setHsp_evalue(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_EVALUE)));
				h.setHsp_gaps(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_GAPS)));
				h.setHsp_hit_from(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_HIT_FROM)));
				h.setHsp_hit_to(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_HIT_TO)));
				h.setHsp_hseq(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_HSEQ)));
				h.setHsp_midline(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_MIDLINE)));
				h.setHsp_qseq(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_QSEQ)));
				h.setHsp_query_from(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_QUERY_FROM)));
				h.setHsp_query_to(hspsCursor.getString(hspsCursor.getColumnIndex(ResultDatabase.KEY_QUERY_TO)));
				hsps.add(h);
			}while(hspsCursor.moveToNext());
		}
		db.close();
		return hsps;
	}
	
	public static void saveImage(Context context, long id, byte[] img){
		String filename = "result-" + id + "-img";
		try {
			FileOutputStream outputStream = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			outputStream.write(img);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveImagePreProcessed(Context context, long id, byte[] img){
		String filename = "result-" + id + "-imgPre";
		try {
			FileOutputStream outputStream = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			outputStream.write(img);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap loadImage(Context context, long id){
		String filename = "result-" + id + "-img";
		byte[] bytes = null;
		try {
			InputStream is = context.openFileInput(filename);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			}
			bytes = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(bytes != null)
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}
	
	public static byte[] loadImageBytes(Context context, long id){
		String filename = "result-" + id + "-img";
		byte[] bytes = null;
		try {
			InputStream is = context.openFileInput(filename);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			}
			bytes = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static byte[] loadImageBytesPreProcessed(Context context, long id){
		String filename = "result-" + id + "-imgPre";
		byte[] bytes = null;
		try {
			InputStream is = context.openFileInput(filename);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			}
			bytes = bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public static byte[] getThumbnail(byte[] originalData){
		Bitmap bmp = BitmapFactory.decodeByteArray(originalData, 0, originalData.length);
		Bitmap bmpReduced = Bitmap.createScaledBitmap(bmp, 100, 100, false);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmpReduced.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
}
