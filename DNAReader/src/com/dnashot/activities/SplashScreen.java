package com.dnashot.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dnashot.data.Result;
import com.dnashot.system.ResultManager;
import com.dnashot.v0.R;

public class SplashScreen extends Activity {
	private List<Result> listResults;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				doLoad();

			}
		}, 500);
	}

	private void doLoad(){
		try {
			listResults = ResultManager.loadResults(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
			listResults = new ArrayList<Result>();
		}
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				doGoToMain();
			}
		}, 1000);
		
	}
	
	private void doGoToMain(){
		MainActivity.receivedListResults = listResults;
		Intent it = new Intent(this, MainActivity.class);
		startActivity(it);
		this.finish();
	}
}
