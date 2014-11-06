package com.dnareader.system;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.dnareader.activities.MainActivity;
import com.dnareader.data.Result;

public class ResultManager {
	public static final String FILENAME = "results";
	public static final String DIRECTORY = "/DNAShot";

	public static void saveResult(Context context) {
		List<Result> list = MainActivity.listResults;
		Log.d(MainActivity.TAG, "Result list: " + list.size());
		try {
			FileOutputStream fos = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(list);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearResults(Context context) {
		context.deleteFile(FILENAME);
	}

	@SuppressWarnings("unchecked")
	public static void loadResults(Context context) throws Exception {
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Result> newList = (List<Result>) ois.readObject();
			ois.close();

			MainActivity.listResults = newList;
			Log.d(MainActivity.TAG, "Loaded list with size: " + newList.size());
		} catch (FileNotFoundException e) {
			Log.d(MainActivity.TAG,
					"Result file not found, creating a empty list.");
			MainActivity.listResults = new ArrayList<Result>();
		}
	}
}
