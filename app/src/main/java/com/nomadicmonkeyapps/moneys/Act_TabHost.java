package com.nomadicmonkeyapps.moneys;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import com.nomadicmonkeyapps.moneys.R;

public class Act_TabHost extends TabActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_maintab);
		
		Log.d("zzz", "Act_TabHost");
		
		TabHost tabHost = getTabHost();

		TabSpec screenMainSpec = tabHost.newTabSpec("MainScreen");
		screenMainSpec.setIndicator("Main Menu", getResources().getDrawable(R.drawable.ic_screeen_main));
		Intent tab1Int = new Intent(this, Act_Main.class);
		screenMainSpec.setContent(tab1Int);

		TabSpec screenHistorySpec = tabHost.newTabSpec("History");
		screenHistorySpec.setIndicator("History", getResources().getDrawable(R.drawable.ic_screen_history));
		Intent songsIntent = new Intent(this, Act_History.class);
		screenHistorySpec.setContent(songsIntent);
		
        tabHost.addTab(screenMainSpec); 
        tabHost.addTab(screenHistorySpec); 
        
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.title_bg);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.title_bg);
	}
}
