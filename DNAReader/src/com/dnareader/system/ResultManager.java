package com.dnareader.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	public static void saveResult(Context context) {
		List<Result> list = MainActivity.listResults;
		if (isExternalStorageWritable() && list.size() > 0) {
			try {
				//File sdCard = Environment.getExternalStorageDirectory();
				//File directory = new File(sdCard.getAbsolutePath() + DIRECTORY);
				//directory.mkdirs();

				//File file = new File(directory, FILENAME);
//				FileOutputStream fos = new FileOutputStream(file);
				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(list);
				oos.flush();
				oos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void clearResults(Context context){
		context.deleteFile(FILENAME);
	}

	@SuppressWarnings("unchecked")
	public static void loadResults(Context context) throws Exception {
		if (isExternalStorageReadable()) {
			Log.d(MainActivity.TAG, "Reached this point");
				//File sdCard = Environment.getExternalStorageDirectory();
				//File directory = new File(sdCard.getAbsolutePath() + DIRECTORY);
				//File file = new File(directory, FILENAME);
				//FileInputStream fis = new FileInputStream(file);
			try{
				FileInputStream fis = context.openFileInput(FILENAME);				
				ObjectInputStream ois = new ObjectInputStream(fis);
				List<Result> newList = (List<Result>) ois.readObject();
				ois.close();				
				
				MainActivity.listResults = newList;				
				Log.d(MainActivity.TAG, "Loaded list with size: " + newList.size());
			}catch(FileNotFoundException e){
				Log.d(MainActivity.TAG, "Result file not found, creating a empty list.");
				MainActivity.listResults = new ArrayList<Result>();
			}
				
		}else{
		MainActivity.listResults = new ArrayList<Result>();
		}
		
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
