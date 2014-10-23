package com.dnareader.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;

public class ResultManager {
	public static final String FILENAME = "results";
	public static final String DIRECTORY = "/DNAShot";

	public static void saveResult(Context context, List<Result> list) {
		if (isExternalStorageWritable()) {
			Log.d(MainActivity.TAG, "list:" + list.size());
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File directory = new File (sdCard.getAbsolutePath() + DIRECTORY);
				directory.mkdirs();
				
				File file = new File(directory, FILENAME);
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(list);
				oos.flush();
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Result> loadResults(Context context) {
		if (isExternalStorageReadable()) {
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File directory = new File (sdCard.getAbsolutePath() + DIRECTORY);
				File file = new File(directory, FILENAME);
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				List<Result> newList = (List<Result>) ois.readObject();
				ois.close();
				return newList;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ArrayList<Result>();
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
