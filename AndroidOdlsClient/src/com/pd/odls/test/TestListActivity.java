package com.pd.odls.test;

import com.pd.odls.test.gait.GaitTestActivity;
import com.pd.odls.test.handtremor.HandTremorTestActivity;
import com.pd.odls.test.legtremor.LegTremorTestActivity;
import com.pd.odls.test.tapping.TappingTestActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestListActivity extends ListActivity {

	private static final int REQUEST_TEST = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set test name array
		String[] testNames = {"Test hand tremor", "Test gait",
				"Test finger tapping", "Test leg tremor"};
		
		//create the arrayAdapter to display all test names in a ListView
		this.setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, testNames));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		clickDelegate(position);
//		Object selected = this.getListAdapter().getItem(position);
//		
//		Toast.makeText(this, selected.toString(), Toast.LENGTH_SHORT).show();
	}
	
	public void clickDelegate(int position) {
		Intent i = null;
		switch(position) {
		case 0:
			i = new Intent(this, HandTremorTestActivity.class);
			break;
		case 1:
			i = new Intent(this, GaitTestActivity.class);
			break;
		case 2:
			i = new Intent(this, TappingTestActivity.class);
			break;
		case 3:
			i = new Intent(this, LegTremorTestActivity.class);
			break;
		default:
			return;
		}
		
		this.startActivityForResult(i, REQUEST_TEST);
	}
}
