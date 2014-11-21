package com.dnashot.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.dnareader.v0.R;
import com.dnashot.data.Result;

public class ResultActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	
	private static int resultPosition;
	private static long resultId;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_result);
		
		// Initilization
		
		// Tab titles		
		String[] tabs = { getResources().getString(R.string.title_tab_result), getResources().getString(R.string.title_tab_debug)};
		
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
		
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			try {
				
				resultPosition = bundle.getInt("position");
				Result target = MainActivity.listResults.get(resultPosition);
				setTitle(getResources().getString(R.string.result_id)
						+ target.getId());				
				resultId = target.getLongId();
			} catch (Exception e) {
				Log.e("DNAReader", "Error: " + e.getMessage());
				e.printStackTrace();
				this.finish();
			}
		}
		
	}

	@Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    
    public static class TabsPagerAdapter extends FragmentPagerAdapter {
    	 
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
     
        @Override
        public Fragment getItem(int index) {
     
            switch (index) {
            case 0:
                // Top Rated fragment activity
                return FragmentResultHits.newInstance(resultPosition);
            case 1:
                // Games fragment activity
                return FragmentResultDebug.newInstance(resultPosition);      
            }
     
            return null;
        }
     
        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }
     
    }
    
    @Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete_result:
			DialogFragment clearResults = new DeleteResultDialogFragment(this, resultId, resultPosition, true);
			clearResults.setCancelable(true);
			clearResults.show(getFragmentManager(), MainActivity.TAG);
			return true;
		}
		return false;
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.result, menu);
		return true;
	}
}
