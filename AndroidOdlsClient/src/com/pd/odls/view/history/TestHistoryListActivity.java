package com.pd.odls.view.history;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.pd.odls.R;
import com.pd.odls.domain.model.Assessment;
import com.pd.odls.domain.model.User;
import com.pd.odls.utils.http.OdlsHttpClientHelper;
import com.pd.odls.utils.sqlite.OdlsDbAdapter;

public class TestHistoryListActivity extends Activity {
	
	private static final int CONTEXT_MENU_DETAIL = 0;
	private static final int CONTEXT_MENU_SEND = 1;
	private static final int CONTEXT_MENU_DELETE = 2;
	
	//Constant dialog value
	private static final int DLG_SEND_FAIL = 406;
	private static final int DLG_SEND_IN_PROGRESS = 17;
	private static final int DLG_SEND_SUCCESS = 200;
	private static final int DLG_SEND_ERROR = 11;
	
	private OdlsDbAdapter databaseManager;
	private ListView testsListView;
	private EditText search;
	private TestHistoryListCursorAdapter adapter;

	/**
	 * Handler to deal with result of sending file to server
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case DLG_SEND_SUCCESS:showDialog(DLG_SEND_SUCCESS);
				break;
			case DLG_SEND_FAIL:showDialog(DLG_SEND_FAIL);
				break;
			default:
				return;
			}
		}		
	};

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
				  arg0.add(Menu.NONE, CONTEXT_MENU_DETAIL, Menu.FIRST, "Detail");
				  arg0.add(Menu.NONE, CONTEXT_MENU_SEND, Menu.FIRST + 1, "Send");
                  arg0.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.FIRST + 2, "Delete");				
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
    	Cursor c = ((CursorAdapter)testsListView.getAdapter()).getCursor();
        c.moveToPosition(menuInfo.position);
        Assessment selectedTest = new Assessment();
        selectedTest.instantiateTest(c);
        switch (item.getItemId()) {
        case CONTEXT_MENU_DETAIL:
        	Log.i(this.getClass().getName(), "Detail:" + selectedTest.getTesterID() + selectedTest.getTestID());
        	//TODO: show detail result of selected test
        	return true;
        case CONTEXT_MENU_SEND:
        	Log.i(this.getClass().getName(),"Send:" + selectedTest.getTesterID() + selectedTest.getTestID());      	
        	//send test artifact xml file to server
    		Thread thread = new Thread(new SendFileToServerRunnable(selectedTest));
    		thread.start();        	
        	return true;
        case CONTEXT_MENU_DELETE:
        	/* Remove test from the sqlite database and the list view.*/
        	databaseManager.deleteTest(selectedTest.getTestID());
        	c.requery();
        	adapter.notifyDataSetChanged();
        	return true;
        }
        return false;
	}

	/**
	 * Function to send test xml instance to server
	 * @param test
	 * @return Server response code
	 */
	private int sendTestToServer(Assessment test) {
		if(test == null) {
			return DLG_SEND_FAIL;
		}
		
    	String url = PreferenceManager.getDefaultSharedPreferences(this).getString("server.send", "http://10.0.2.2:8080/Odls/send");
		String file = test.getTesterID() + "_" + test.getTestID() + ".xml";
		FileInputStream fis = null;
		try {
			if((fis = openFileInput(file)) != null) {
				byte[] fileBytes = IOUtils.toByteArray(fis);
				
				MultipartEntity fileEntity = new MultipartEntity();
				ByteArrayBody bab = new ByteArrayBody(fileBytes, file);
				fileEntity.addPart(User.USER_NAME, new StringBody(test.getTesterID()));
				fileEntity.addPart("file", bab);
				return OdlsHttpClientHelper.getHttpPostResponse(url, fileEntity).getStatusLine().getStatusCode();				
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DLG_SEND_FAIL;
	}
	
	/**
	 * Retrieve tests from database, bind the query cursor to CursorAdapter
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
		startManagingCursor(cursor);
		
	    //Create FilterQueryProvider
	    TestListFilterQueryProvider filterProvider = new TestListFilterQueryProvider(this, databaseManager);
		
	    //Create and set Test list adapter
	    adapter = new TestHistoryListCursorAdapter(this, cursor, filterProvider, R.layout.test_history_list_item);
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = null;
		Builder builder = null;
		switch (id) {
		case DLG_SEND_FAIL:
			// Create AlterDialog
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder.setMessage("Error to access server. Please check your internet setting!");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		case DLG_SEND_IN_PROGRESS:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			return progressDialog;
		case DLG_SEND_SUCCESS:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("");
			builder.setMessage("Send file successful!");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		case DLG_SEND_ERROR:
			// Create AlterDialog
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Error");
			builder.setMessage("Application error. Sending file is terminated!");
			
			builder.setCancelable(true);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//TODO: OK button trigered event
				}
			});
			dialog = builder.create();
			return dialog;
		}
		return dialog;
	}



	/**
	 * Implementation of a runnable to delegate the send file to server thread
	 * @author Pan
	 *
	 */
	private class SendFileToServerRunnable implements Runnable {
		private Assessment test;
		
		public SendFileToServerRunnable(Assessment test) {
			this.test = test;
		}
		
		public void run() {
			handler.sendEmptyMessage(sendTestToServer(test));
        	Log.i(this.getClass().getName(),"Complete sending:");
		}
		
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
