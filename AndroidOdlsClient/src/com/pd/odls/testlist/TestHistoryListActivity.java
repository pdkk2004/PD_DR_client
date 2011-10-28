package com.pd.odls.testlist;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dp.odls.model.Test;
import com.dp.odls.model.User;
import com.dp.odls.sqlite.OdlsDbAdapter;
import com.pd.odls.R;

public class TestHistoryListActivity extends Activity {
	
	private ArrayList<Test> listContent;
	private OdlsDbAdapter databaseManager;
	private ListView testsListView;
	private EditText search;
	

	@Override
	protected void onResume() {
		this.retrieveTests();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tests_history);
		testsListView = (ListView)findViewById(R.id.list_test);
		search = (EditText)findViewById(R.id.edit_search);
	    
	    //Prepare database
	    databaseManager = new OdlsDbAdapter(this);
		databaseManager.open();
		
		//Add onChange listener to search field
		search.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				//TODO response to search text change
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// do nothing
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// do nothing
			}
			
		});
	}

	/**
	 * Retrieve tests from database
	 */
	private void retrieveTests() {		
		//Get userName from SharedPreference
	    SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(this);
	    String userName = userPreference.getString(User.USER_NAME, "n/a");
	    
	    //Fetch newest 20 tests history query string
	    String sql = "SELECT " 
	    	+ OdlsDbAdapter.FIELD_TESTER_ID + ", "
	    	+ OdlsDbAdapter.FIELD_TEST_ID + ", "
	    	+ OdlsDbAdapter.FIELD_TYPE + ", "
	    	+ OdlsDbAdapter.FIELD_DATE + ", "
	    	+ OdlsDbAdapter.FIELD_BEGIN_TIME + ", "
	    	+ OdlsDbAdapter.FIELD_END_TIME + ", "
	    	+ OdlsDbAdapter.FIELD_DURATION + ", "
	    	+ OdlsDbAdapter.FIELD_EXPLANATION + ", "
	    	+ OdlsDbAdapter.FIELD_SAMPLE_RATE + ", "
	    	+ OdlsDbAdapter.FIELD_SCALE + " from "
	    	+ OdlsDbAdapter.DATABASE_TABLE
	    	+ " WHERE " + OdlsDbAdapter.FIELD_TESTER_ID 
	    	+ " = '" + userName + "'"
	    	+ " ORDER BY " 
	    	+ OdlsDbAdapter.FIELD_BEGIN_TIME + " DESC "
	    	+ " LIMIT 20;";
	    
	    //TODO: Test purpose, should be deleted in production release
//	    String sqltest = "SELECT data from " + OdlsDbAdapter.DATABASE_TABLE + ";";
//	    Cursor c = databaseAdapter.fetchTest(sqltest);
//	    c.moveToFirst();
//	    while(!c.isAfterLast()) {
//	    	int index = c.getColumnIndex(OdlsDbAdapter.FIELD_DATA);
//	    	byte[] data = c.getBlob(index);
//	    	System.out.println("retrieved:" + Arrays.toString(data));
//	    	c.moveToNext();
//	    }
	    
	    
	    //Get query result cursor
	    Cursor cursor = databaseManager.fetchTest(sql);
	    //Fill the test list content from query result
		listContent = fillTestArray(cursor);
		cursor.close();
		
		//Prepare the ListView Adapter using simple list. Have been updated by using
		//customized TestItemAdaptor
//		String[] from = new String[]{"imageView_type", "textView_type", "textView_value", "textView_date"};
//		int[] to = new int[] {R.id.imageView_type, R.id.textView_type, R.id.textView_value, R.id.textView_date};
//		
//		List<HashMap<String, String>> fillMap = fillListMap(listContent);
//		
//		ListAdapter adapter = new SimpleAdapter(this, fillMap, 
//				R.layout.test_list_item, from, to);
		
		ListAdapter adapter = new TestItemAdaptor(this, listContent);
		testsListView.setAdapter(adapter);
	}
	
	/**
	 * Use query result to instantiate instances of Test
	 * @param cursor
	 * @return
	 */
	public ArrayList<Test> fillTestArray(Cursor cursor) {
		ArrayList<Test> list = new ArrayList<Test>();
		
		if(cursor != null) {
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				Test test = new Test();
				test.instantiateTest(cursor);
				list.add(test);
				cursor.moveToNext();
			}
		}
		return list;
	}
	
	@Override	
	public void onDestroy() {
		listContent = null;
		databaseManager.close();
		databaseManager = null;
		super.onDestroy();
	}

	@Override
	public void finish() {
		databaseManager.close();
		super.finish();
	}
	

	//Prepare List adapter map to be consumed by ListAdapter
//	private List<HashMap<String, String>> fillListMap(ArrayList<? extends Test> testList) {
//		List<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
//		
//		for(Test t: testList) {
//			HashMap<String, String> map = new HashMap<String, String>();
//			map.put("imageView_type", this.getResources().getDrawable(t.getTestIcon()));
//			map.put("textView_type", Test.TEST_TYPES[t.getType()]);
//			map.put("textView_value", t.getScale() == null ? "N/A" : String.valueOf(t.getScale()));
//			
//			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//			String date = sdf.format(t.getDate());
//			sdf = new SimpleDateFormat("HH:mm");
//			String time = sdf.format(t.getBeginTime());
//			
//			map.put("textView_date", date + " @" + time);
//			mapList.add(map);
//		}
//		return mapList;
//	}
}
