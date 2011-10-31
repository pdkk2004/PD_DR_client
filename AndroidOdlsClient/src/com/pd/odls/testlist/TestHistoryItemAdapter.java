package com.pd.odls.testlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pd.odls.R;
import com.pd.odls.model.Test;

public class TestHistoryItemAdapter extends ArrayAdapter<Test>{

    private static LayoutInflater inflater = null;
    private Filter filter;
    private ArrayList<Test> items;
    private ArrayList<Test> filtered;
    
    public TestHistoryItemAdapter(Context context, int resourceId, ArrayList<Test> items) {
        super(context, resourceId, items);
        this.filtered = items;
        this.items = new ArrayList<Test>();
        this.items.addAll(items);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = inflater.inflate(R.layout.test_history_list_item, null);

        TextView testDate=(TextView)view.findViewById(R.id.textView_date);
        TextView testType=(TextView)view.findViewById(R.id.textView_type);
        TextView testValue=(TextView)view.findViewById(R.id.textView_value);
        ImageView testImage=(ImageView)view.findViewById(R.id.imageView_type);
        
        Test t = getItem(position);
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
        Drawable drawable = this.getContext().getResources().getDrawable(t.getTestIcon());
        testImage.setImageDrawable(drawable);
        return view;
    }


	@Override
	public Filter getFilter() {
		if(filter == null)
			this.filter = new TestFilter();
		return filter;
	}
	
	/**
	 * Customized filter to filter test
	 * @author Pan
	 *
	 */
	private class TestFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
		    // NOTE: this function is *always* called from a background thread, and             
			// not the UI thread.
			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if(constraint != null && constraint.toString().length() > 0) {
				ArrayList<Test> filt = new ArrayList<Test>();   
				for(int i = 0, l = items.size(); i < l; i++) {
					Test t = items.get(i);
					if(Test.TEST_TYPES[t.getType()].toLowerCase().contains(constraint)
							|| t.getExplaination().contains(constraint))
						filt.add(t);               
					}
				result.count = filt.size();
				result.values = filt;
				}
			else {
				synchronized(items) {
					result.values = items;
					result.count = items.size();
				}
			}
			return result; 
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
			filtered = (ArrayList<Test>)results.values;
			notifyDataSetChanged();
			clear();
			for(int i = 0, l = filtered.size(); i < l; i++)
				add(filtered.get(i));
			notifyDataSetInvalidated(); 			
		}
		
	}
	
    
}
