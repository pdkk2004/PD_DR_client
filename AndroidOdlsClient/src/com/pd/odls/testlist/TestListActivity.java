package com.pd.odls.testlist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.pd.odls.R;
import com.pd.odls.test.gait.GaitTestActivity;
import com.pd.odls.test.handtremor.HandTremorTestActivity;
import com.pd.odls.test.legtremor.LegTremorTestActivity;
import com.pd.odls.test.tapping.TappingTestActivity;

public class TestListActivity extends ListActivity {

	private static final int REQUEST_TEST = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set test name array
		String[] testNames = {"Test hand tremor", "Test gait",
				"Test finger tapping", "Test leg tremor"};
		
		int[] testImage = {R.drawable.hand_tremor, R.drawable.gait_difficulty,
				R.drawable.finger_tapping, R.drawable.leg_tremor};
		
		//create the arrayAdapter to display all test names in a ListView
		this.setListAdapter(new TestTypeItemAdaptor(this, testNames, testImage));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		clickDelegate(position);
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
	
	/**
	 * Test list adapter to show all available tests. For each test, the test name and test icon is displayed.
	 * @author Pan
	 *
	 */
	private class TestTypeItemAdaptor extends BaseAdapter{

		private Context context;
		private String[] text;
		private int[] images;

		public TestTypeItemAdaptor(Context context, String[] testNames, int[] testImages) {
			this.context = context;
			this.text = testNames;
			this.images = testImages;
		}

		public int getCount() {
			return text.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(convertView == null) {
				LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.test_type_list_item, null);
			}
			
			TextView testType=(TextView)view.findViewById(R.id.textView_text);
			ImageView testImage=(ImageView)view.findViewById(R.id.imageView_image);

			//bind test date text
			testType.setText(text[position]);

			//bind test type image
			Drawable drawable = context.getResources().getDrawable(images[position]);
			testImage.setImageDrawable(drawable);
			return view;
		}

	}
}
