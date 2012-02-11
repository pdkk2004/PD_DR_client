package com.pd.odls.testlist;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.pd.odls.R;
import com.pd.odls.model.Test;

public class TestHistoryListCursorAdapter extends CursorAdapter {
	
    private Context context;
    private FilterQueryProvider filter;
	private int layoutId;

    
	public TestHistoryListCursorAdapter(Context context, Cursor c, FilterQueryProvider filter, int layoutId) {
		super(context, c);
        this.context = context;
        this.layoutId = layoutId;
        this.filter = filter;
        this.setFilterQueryProvider(getFilterQueryProvider());
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		Cursor c = getCursor();

		TextView testDate=(TextView)view.findViewById(R.id.textView_date);
		TextView testType=(TextView)view.findViewById(R.id.textView_type);
		TextView testValue=(TextView)view.findViewById(R.id.textView_value);
		ImageView testImage=(ImageView)view.findViewById(R.id.imageView_type);

		Test test = new Test();
		test.instantiateTest(c);
		//bind test date text
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(test.getDate());
		sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(test.getBeginTime());
		testDate.setText(date + " @" + time);

		//bind test type text
		String typeNameToShow = Test.TEST_TYPES[test.getType()].split(" ")[0];
		testType.setText("ID:" + test.getTesterID() +  "  " + typeNameToShow);

		//bind test evaluation value
		testValue.setText(test.getScale() == null ? "N/A" : String.valueOf(test.getScale()));

		//bind test type image
		Drawable drawable = this.getContext().getResources().getDrawable(test.getTestIcon());
		testImage.setImageDrawable(drawable);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        Cursor c = getCursor();
        final LayoutInflater inflater = LayoutInflater.from(context);
        
        View view = inflater.inflate(layoutId, null);

        TextView testDate=(TextView)view.findViewById(R.id.textView_date);
        TextView testType=(TextView)view.findViewById(R.id.textView_type);
        TextView testValue=(TextView)view.findViewById(R.id.textView_value);
        ImageView testImage=(ImageView)view.findViewById(R.id.imageView_type);
                
        Test test = new Test();
        test.instantiateTest(c);
        //bind test date text
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(test.getDate());
		sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(test.getBeginTime());
        testDate.setText(date + " @" + time);
        
        //bind test type text
		String typeNameToShow = Test.TEST_TYPES[test.getType()].split(" ")[0];
		testType.setText("ID:" + test.getTesterID() +  "  " + typeNameToShow);
        
        //bind test evaluation value
        testValue.setText(test.getScale() == null ? "N/A" : String.valueOf(test.getScale()));
        
        //bind test type image
        Drawable drawable = this.getContext().getResources().getDrawable(test.getTestIcon());
        testImage.setImageDrawable(drawable);
        return view;
	}


	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return getFilterQueryProvider().runQuery(constraint);
	}
	
	@Override
	public FilterQueryProvider getFilterQueryProvider() {
		return filter;
	}
	

}
