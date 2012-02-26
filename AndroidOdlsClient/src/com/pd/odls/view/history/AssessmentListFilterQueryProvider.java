package com.pd.odls.view.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.widget.FilterQueryProvider;

import com.pd.odls.domain.model.User;
import com.pd.odls.utils.sqlite.OdlsDbAdapter;

class AssessmentListFilterQueryProvider implements FilterQueryProvider {
	
	private Context context;
    private OdlsDbAdapter databaseManager;
    	
	public AssessmentListFilterQueryProvider(Context context,
			OdlsDbAdapter databaseManager) {
		super();
		this.context = context;
		this.databaseManager = databaseManager;
	}


	public Cursor runQuery(CharSequence constraint) {
	    // NOTE: this function is *always* called from a background thread, and             
		// not the UI thread.
	    SharedPreferences userPreference = PreferenceManager.getDefaultSharedPreferences(context);
	    String userName = userPreference.getString(User.USER_NAME, "n/a");
		constraint = constraint.toString().toLowerCase();
		String query  = null;
		if(constraint != null) {
			if(userName.equals("admin")) {
				query = "SELECT " 
						+ OdlsDbAdapter.FIELD_TESTER_ID + ", "
						+ OdlsDbAdapter.FIELD_TEST_ID + ", "
						+ OdlsDbAdapter.FIELD_TYPE + ", "
						+ OdlsDbAdapter.FIELD_BEGIN_TIME + ", "
						+ OdlsDbAdapter.FIELD_END_TIME + ", "
						+ OdlsDbAdapter.FIELD_DURATION + ", "
						+ OdlsDbAdapter.FIELD_EXPLANATION + ", "
						+ OdlsDbAdapter.FIELD_SAMPLE_RATE + ", "
						+ OdlsDbAdapter.FIELD_SCALE + " FROM "
						+ OdlsDbAdapter.DATABASE_TABLE
						+ " WHERE " + "LOWER(" + OdlsDbAdapter.FIELD_TYPE
						+ ") LIKE '%" + constraint + "%'" 
						+ " OR LOWER(" + OdlsDbAdapter.FIELD_TESTER_ID
						+ ") LIKE '%" + constraint + "%'"
						+ " ORDER BY " 
						+ OdlsDbAdapter.FIELD_TESTER_ID + ", " + OdlsDbAdapter.FIELD_BEGIN_TIME + " DESC;";
			}
			else {
				query = "SELECT " 
						+ OdlsDbAdapter.FIELD_TESTER_ID + ", "
						+ OdlsDbAdapter.FIELD_TEST_ID + ", "
						+ OdlsDbAdapter.FIELD_TYPE + ", "
						+ OdlsDbAdapter.FIELD_BEGIN_TIME + ", "
						+ OdlsDbAdapter.FIELD_END_TIME + ", "
						+ OdlsDbAdapter.FIELD_DURATION + ", "
						+ OdlsDbAdapter.FIELD_EXPLANATION + ", "
						+ OdlsDbAdapter.FIELD_SAMPLE_RATE + ", "
						+ OdlsDbAdapter.FIELD_SCALE + " FROM "
						+ OdlsDbAdapter.DATABASE_TABLE
						+ " WHERE " + "LOWER(" + OdlsDbAdapter.FIELD_TYPE
						+ ") LIKE '%" + constraint + "%'" 
						+  " AND " + OdlsDbAdapter.FIELD_TESTER_ID
						+ " = '" + userName + "'"
						+ " ORDER BY " 
						+ OdlsDbAdapter.FIELD_BEGIN_TIME + " DESC;";
			}
		}
        return databaseManager.fetchTest(query);
	}
}
