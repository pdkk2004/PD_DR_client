package com.pd.odls.testlist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.pd.odls.R;
import com.pd.odls.model.Test;
import com.pd.odls.model.User;
import com.pd.odls.sqlite.OdlsDbAdapter;

public class TestHistoryListActivity extends Activity {
	
	private static final int CONTEXT_MENU_DETAIL = 0;
	private static final int CONTEXT_MENU_SEND = 1;
	private static final int CONTEXT_MENU_DELETE = 2;
	
	private OdlsDbAdapter databaseManager;
	private ListView testsListView;
	private EditText search;
	private TestHistoryListCursorAdapter adapter;

	@Override
	protected void onResume() {
		this.bindAdapter();
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
		
		//Add context menu to list item
		testsListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu arg0, View arg1,
					ContextMenuInfo arg2) {
				  arg0.setHeaderTitle("Available Actions");
				  arg0.add(Menu.NONE, CONTEXT_MENU_DETAIL, Menu.FIRST, "Show detail");
				  arg0.add(Menu.NONE, CONTEXT_MENU_SEND, Menu.FIRST + 1, "Send test to server");
                  arg0.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.FIRST + 2, "Delete this test!");				
			}

		});
		
		//Add onChange listener to search field
		search.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				adapter.getFilter().filter(s);
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
		
		//Add list item short click listener
		testsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			}
			
		});
		
		//Add list item long click listener
		testsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
			
		});
	}
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        /* Switch on the ID of the item, to get what the user selected. */
        Test selected = (Test)adapter.getItem(menuInfo.position);
        switch (item.getItemId()) {
        case CONTEXT_MENU_DETAIL:
        	Log.i(this.getClass().getName(), "Detail:" + selected.getTesterID() + selected.getTestID());
        	//TODO: show detail result of selected test
        	Toast.makeText(this, "Detail:" + selected.getTesterID() + selected.getTestID(), Toast.LENGTH_LONG);
        	return true;
        case CONTEXT_MENU_SEND:
        	Log.i(this.getClass().getName(),"Send:" + selected.getTesterID() + selected.getTestID());
        	//TODO: send test xml artifact to server
        	Toast.makeText(this, "Send:" + selected.getTesterID() + selected.getTestID(), Toast.LENGTH_LONG);
        	return true;
        case CONTEXT_MENU_DELETE:
        	/* Get the selected item out of the Adapter by its position. */
        	/* Remove test from the list.*/
//        	adapter.remove(selected);
        	adapter.notifyDataSetChanged();
        	return true; /* true means: "we handled the event". */
        }
        return false;
	}
	
	/**
	 * Retrieve tests from database
	 */
	private void bindAdapter() {		
		//Get userName from SharedPreference
	    SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(this);
	    String userName = userPreference.getString(User.USER_NAME, "n/a");
	    String sql = null;
	    
	    if(userName.equals("admin")) {
		    //Fetch all tests from database, require administrator authority
	    	sql = "SELECT " 
	    			+ OdlsDbAdapter.FIELD_TESTER_ID + ", "
	    			+ OdlsDbAdapter.FIELD_TEST_ID + ", "
	    			+ OdlsDbAdapter.FIELD_TYPE + ", "
	    			+ OdlsDbAdapter.FIELD_DATE + ", "
	    			+ OdlsDbAdapter.FIELD_BEGIN_TIME + ", "
	    			+ OdlsDbAdapter.FIELD_END_TIME + ", "
	    			+ OdlsDbAdapter.FIELD_DURATION + ", "
	    			+ OdlsDbAdapter.FIELD_EXPLANATION + ", "
	    			+ OdlsDbAdapter.FIELD_SAMPLE_RATE + ", "
	    			+ OdlsDbAdapter.FIELD_SCALE + " FROM "
	    			+ OdlsDbAdapter.DATABASE_TABLE
	    			+ " ORDER BY " 
	    			+ OdlsDbAdapter.FIELD_BEGIN_TIME + " DESC;";
	    }
	    else {
		    //Fetch only tests for specific userName from database, require end user authority
	    	sql = "SELECT " 
	    			+ OdlsDbAdapter.FIELD_TESTER_ID + ", "
	    			+ OdlsDbAdapter.FIELD_TEST_ID + ", "
	    			+ OdlsDbAdapter.FIELD_TYPE + ", "
	    			+ OdlsDbAdapter.FIELD_DATE + ", "
	    			+ OdlsDbAdapter.FIELD_BEGIN_TIME + ", "
	    			+ OdlsDbAdapter.FIELD_END_TIME + ", "
	    			+ OdlsDbAdapter.FIELD_DURATION + ", "
	    			+ OdlsDbAdapter.FIELD_EXPLANATION + ", "
	    			+ OdlsDbAdapter.FIELD_SAMPLE_RATE + ", "
	    			+ OdlsDbAdapter.FIELD_SCALE + " FROM "
	    			+ OdlsDbAdapter.DATABASE_TABLE
	    			+ " WHERE " + OdlsDbAdapter.FIELD_TESTER_ID 
	    			+ " = '" + userName + "'"
	    			+ " ORDER BY " 
	    			+ OdlsDbAdapter.FIELD_BEGIN_TIME + " DESC;";
	    }	    
	    
	    //Get query result cursor
	    Cursor cursor = databaseManager.fetchTest(sql);
		
	    //Create FilterQueryProvider
	    TestListFilterQueryProvider provider = new TestListFilterQueryProvider(this, databaseManager);
		
	    //Create and set Test list adapter
	    adapter = new TestHistoryListCursorAdapter(this, cursor, provider, R.layout.test_history_list_item);
		testsListView.setAdapter(adapter);
	}
	
	/**
	 * Use query result to instantiate instances of Test
	 * @param cursor
	 * @return
	 */
//	public ArrayList<Test> fillTestArray(Cursor cursor) {
//		ArrayList<Test> list = new ArrayList<Test>();
//		
//		if(cursor != null) {
//			cursor.moveToFirst();
//			while(!cursor.isAfterLast()) {
//				Test test = new Test();
//				test.instantiateTest(cursor);
//				list.add(test);
//				cursor.moveToNext();
//			}
//		}
//		return list;
//	}
	
	@Override	
	public void onDestroy() {
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
