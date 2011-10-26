package com.pd.odls.main;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.pd.odls.R;
import com.pd.odls.scale.FallsEvaluationActivity;
import com.pd.odls.test.TestHistoryListActivity;
import com.pd.odls.test.TestListActivity;

public class MainControlActivity extends TabActivity {
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation);
		
		//create reusable resource to bind tab content view
		Resources res = this.getResources();
		TabHost tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		
		//bind TestListActivity to tab_1
		intent = new Intent().setClass(this, TestListActivity.class);
	    spec = tabHost.newTabSpec("Test")
	    		.setIndicator("Tests", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);    
	    tabHost.addTab(spec);
	    
	    //bind falls efficacy scale evaluation to tab_2
		intent = new Intent().setClass(this, FallsEvaluationActivity.class);
	    spec = tabHost.newTabSpec("Message")
	    		.setIndicator("Messages", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);    
	    tabHost.addTab(spec);
	    
	    //bind test history view to tab_3
	    intent = new Intent().setClass(this, TestHistoryListActivity.class);
	    spec = tabHost.newTabSpec("History Test")
	    		.setIndicator("History", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
		
	}
}
