package com.dnareader.system;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dnareader.data.Result;

public class ResultManager {
	public static void saveResult(Context context,List<Result> list) {
		try {
			FileOutputStream foo = context.openFileOutput("results",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(foo);
			oos.writeObject(list);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Result> loadResults(Context context) {
		try {
			InputStream is = context.openFileInput("results");
			InputStream buffer = new BufferedInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(buffer);
			List<Result> newList = (List<Result>) ois.readObject();
			ois.close();
			return newList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Result>();
	}
}
