package com.pd.odls.testlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dp.odls.model.Test;
import com.pd.odls.R;

public class TestItemAdaptor extends BaseAdapter{

    private Context context;
    private ArrayList<Test> tests;
    private static LayoutInflater inflater=null;
    
    public TestItemAdaptor(Context context, ArrayList<Test> tests) {
        this.context = context;
        this.tests = tests;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return tests.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = inflater.inflate(R.layout.test_list_item, null);

        TextView testDate=(TextView)view.findViewById(R.id.textView_date);
        TextView testType=(TextView)view.findViewById(R.id.textView_type);
        TextView testValue=(TextView)view.findViewById(R.id.textView_value);
        ImageView testImage=(ImageView)view.findViewById(R.id.imageView_type);
        
        Test t = tests.get(position);
        //bind test date text
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String date = sdf.format(t.getDate());
		sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(t.getBeginTime());
        testDate.setText(date + " @" + time);
        
        //bind test type text
        testType.setText(Test.TEST_TYPES[t.getType()]);
        
        //bind test evaluation value
        testValue.setText(t.getScale() == null ? "N/A" : String.valueOf(t.getScale()));
        
        //bind test type image
        Drawable drawable = context.getResources().getDrawable(t.getTestIcon());
        testImage.setImageDrawable(drawable);
        return view;
    }
	
}
