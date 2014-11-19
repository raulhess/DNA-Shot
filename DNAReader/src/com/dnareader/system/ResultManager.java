package com.dnareader.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Hit;
import com.dnareader.data.Hsp;
import com.dnareader.data.Result;

public class ResultManager {
	public static final String FILEPREFIX = "result-";
	public static final String IMG = "-img";
	public static final String PREPROCESSED_IMG = "-imgPre";
	public static final String THUMBNAIL = "-imgThumbnail";
	public static final String DIRECTORY = "/DNAShot/";

	private static Cursor resultsCursor;
	private static Cursor hitsCursor;
	private static Cursor hspsCursor;

	public static long addResult(Context context, Result r) {
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		long id = db.insertResult(r);
		db.close();
		saveImage(context, FILEPREFIX + id + IMG, r.getImage());
		return id;
	}

	public static void addHits(Context context, Result r) {
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		for (Hit hit : r.getHits()) {
			long hitId = db.insertHit(hit, r.getLongId());
			Log.d(MainActivity.TAG, "Saved 'hit' with id = " + hitId);
			for (Hsp hsp : hit.getHsps()) {
				long hspId = db.insertHsp(hsp, hitId);
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

	public static void updateResultState(Context context, Result r) {
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		boolean updated = db.updateResultState(r.getLongId(), r.getState());
		db.close();
		if (updated)
			Log.d(MainActivity.TAG,
					"Updated result state with id = " + r.getLongId());
		else
			Log.d(MainActivity.TAG, "Couldn't update result state with id = "
					+ r.getLongId());
	}

	public static void updateResult(Context context, Result r) {
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		boolean updated = db.updateResult(r);
		db.close();
		if (updated)
			Log.d(MainActivity.TAG, "Updated result with id = " + r.getLongId());
		else
			Log.d(MainActivity.TAG,
					"Couldn't update result with id = " + r.getLongId());
	}

	public static List<Result> loadResults(Context context) {
		List<Result> results = new ArrayList<Result>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		resultsCursor = db.loadResults();
		if (resultsCursor.moveToFirst()) {
			do {
				Result r = new Result();
				r.setId(resultsCursor.getLong(resultsCursor
						.getColumnIndex(ResultDatabase.KEY_ID)));
				Log.d(MainActivity.TAG, "Loading result with id = " + r.getId());
				r.setState(resultsCursor.getInt(resultsCursor
						.getColumnIndex(ResultDatabase.KEY_STATE)));
				r.setOcrText(resultsCursor.getString(resultsCursor
						.getColumnIndex(ResultDatabase.KEY_OCR)));
				r.setBlastXML(resultsCursor.getString(resultsCursor
						.getColumnIndex(ResultDatabase.KEY_XML)));
				r.setThumbnail(loadThumbnail(context, r.getLongId()));

				results.add(0, r);
			} while (resultsCursor.moveToNext());
		}
		db.close();
		return results;
	}

	public static List<Hit> loadHits(Context context, long id) {
		List<Hit> hits = new ArrayList<Hit>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		hitsCursor = db.loadHits(id);
		Log.d(MainActivity.TAG, "Loading hits from result with id = " + id);
		Log.d(MainActivity.TAG, "Found: " + hitsCursor.getCount());
		if (hitsCursor.moveToFirst()) {
			do {
				Hit h = new Hit();
				h.setHit_id(hitsCursor.getLong(hitsCursor
						.getColumnIndex(ResultDatabase.KEY_HIT_ID)));
				h.setHit_len(hitsCursor.getString(hitsCursor
						.getColumnIndex(ResultDatabase.KEY_LEN)));
				h.setHit_def(hitsCursor.getString(hitsCursor
						.getColumnIndex(ResultDatabase.KEY_HITNAME)));
				h.setResultId(id);
				h.setHsps(loadHsps(context, h.getLongHit_id()));
				hits.add(h);
			} while (hitsCursor.moveToNext());
		}
		db.close();
		return hits;
	}

	public static List<Hsp> loadHsps(Context context, long id) {
		List<Hsp> hsps = new ArrayList<Hsp>();
		ResultDatabase db = new ResultDatabase(context);
		db.open();
		hspsCursor = db.loadHsps(id);
		if (hspsCursor.moveToFirst()) {
			do {
				Hsp h = new Hsp();
				h.setHitId(id);
				h.setHsp_align_len(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_ALIGN_LEN)));
				h.setHsp_evalue(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_EVALUE)));
				h.setHsp_gaps(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_GAPS)));
				h.setHsp_hit_from(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_HIT_FROM)));
				h.setHsp_hit_to(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_HIT_TO)));
				h.setHsp_hseq(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_HSEQ)));
				h.setHsp_midline(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_MIDLINE)));
				h.setHsp_qseq(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_QSEQ)));
				h.setHsp_query_from(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_QUERY_FROM)));
				h.setHsp_query_to(hspsCursor.getString(hspsCursor
						.getColumnIndex(ResultDatabase.KEY_QUERY_TO)));
				hsps.add(h);
			} while (hspsCursor.moveToNext());
		}
		db.close();
		return hsps;
	}

	private static void saveImage(Context context, String filename, Bitmap img) {
		String fullPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + DIRECTORY;
		Log.d(MainActivity.TAG, "Saving image:" + fullPath + " " + filename);

		try {
			File dir = new File(fullPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			OutputStream fOut = null;
			File file = new File(fullPath, filename);
			file.createNewFile();
			fOut = new FileOutputStream(file);

			img.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
			fOut.flush();
			fOut.close();

			Log.d(MainActivity.TAG, "Saved image:" + fullPath + " " + filename);
		} catch (Exception e) {
			Log.d(MainActivity.TAG, "Couldn't save image:" + fullPath + " "
					+ filename);
			e.printStackTrace();
		}
	}

	private static Bitmap loadImage(Context context, String filename, int adjust) {
		String fullPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + DIRECTORY + filename;
		Log.d(MainActivity.TAG, "Loading image:" + fullPath);
		Bitmap bmp = null;
		try {
			if (isExternalStorageReadable()) {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = adjust;
				bmp = BitmapFactory.decodeFile(fullPath, options);
				if(adjust == 0)
					bmp = Bitmap.createScaledBitmap(bmp, 100, 100, false);
			}
			if (bmp != null) {
				Log.d(MainActivity.TAG, "Succesfuly loaded image:" + fullPath);
			} else {
				Log.d(MainActivity.TAG, "Couldn't load image:" + fullPath);
			}
			return bmp;
		} catch (Exception e) {
			Log.e("getThumbnail() on external storage", e.getMessage());
			Log.d(MainActivity.TAG, "Couldn't load image:" + fullPath);
			return null;
		}
	}

	public static Bitmap loadFullImage(Context context, long id, int adjust) {
		String filename = FILEPREFIX + id + IMG;
		return loadImage(context, filename, adjust);
	}

	public static void saveFullImage(Context context, long id, Bitmap bmp) {
		String filename = FILEPREFIX + id + IMG;
		saveImage(context, filename, bmp);
	}

	public static void savePreImage(Context context, long id, Bitmap bmp) {
		String filename = FILEPREFIX + id + PREPROCESSED_IMG;
		saveImage(context, filename, bmp);
	}

	public static Bitmap loadThumbnail(Context context, long id) {
		String filename = FILEPREFIX + id + IMG;
		return loadImage(context, filename, 0);
	}

	public static Bitmap loadPreImage(Context context, long id, int adjust) {
		String filename = FILEPREFIX + id + PREPROCESSED_IMG;
		return loadImage(context, filename, adjust);
	}

	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
