package com.pd.odls.main;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TabHost;

import com.pd.odls.R;
import com.pd.odls.view.assessment.AssessmentListActivity;
import com.pd.odls.view.history.AssessmentHistoryListActivity;

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
		intent = new Intent().setClass(this, AssessmentListActivity.class);
	    spec = tabHost.newTabSpec("Test")
	    		.setIndicator("Tests", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);    
	    tabHost.addTab(spec);
	    
	    //bind falls efficacy scale evaluation to tab_2
		intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("plain/text");
	    spec = tabHost.newTabSpec("Message")
	    		.setIndicator("Messages", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);    
	    tabHost.addTab(spec);
	    
	    //bind test history view to tab_3
	    intent = new Intent().setClass(this, AssessmentHistoryListActivity.class);
	    spec = tabHost.newTabSpec("History Test")
	    		.setIndicator("History", res.getDrawable(R.drawable.tab_icon_selector))
	    		.setContent(intent);
	    tabHost.addTab(spec);
	    
	    tabHost.setCurrentTab(0);
	    
        //load App preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //set the server url
		SharedPreferences.Editor edit = preferences.edit();
		
		//default server_root on local machine
//		String serve_root = "http://10.0.2.2:8080";
		String server_root = "http://192.168.1.101:8080";
		edit.putString("server.root", server_root);
		edit.putString("server.login", server_root + "/Odls/login");
		edit.putString("server.send", server_root + "/Odls/send");
		edit.commit();
		String s = preferences.getString("server.root", "");
	}
}
