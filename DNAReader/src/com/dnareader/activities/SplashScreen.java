package com.dnareader.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dnareader.data.Result;
import com.dnareader.system.ResultManager;
import com.dnareader.v0.R;

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
		}, 1000);
	}
	
	private void doLoad(){
		try {
			listResults = ResultManager.loadResults(getApplicationContext());
		} catch (Exception e) {
			e.printStackTrace();
			listResults = new ArrayList<Result>();
		}
		MainActivity.receivedListResults = listResults;
		Intent it = new Intent(this, MainActivity.class);
		startActivity(it);
		this.finish();
	}
}
