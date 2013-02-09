package com.tyrlib2.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.tyrlib2.demo.example1.ExampleOneActivity;
import com.tyrlib2.demo.example2.ExampleTwoActivity;
import com.tyrlib2.demo.example3.ExampleThreeActivity;
import com.tyrlib2.demo.example4.ExampleFourActivity;

public class MainActivity extends ListActivity {
	
	private static final String ITEM_IMAGE = "item_image";
	private static final String ITEM_TITLE = "item_title";
	private static final String ITEM_SUBTITLE = "item_subtitle";	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		setContentView(R.layout.table_of_contents);
		
		final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		final SparseArray<Class<? extends Activity>> activityMapping = new SparseArray<Class<? extends Activity>>();
		
		int i = 0;
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_TITLE, getText(R.string.example_one));
			item.put(ITEM_SUBTITLE, getText(R.string.example_one_subtitle));
			data.add(item);
			activityMapping.put(i++, ExampleOneActivity.class);			
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_TITLE, getText(R.string.example_two));
			item.put(ITEM_SUBTITLE, getText(R.string.example_two_subtitle));
			data.add(item);
			activityMapping.put(i++, ExampleTwoActivity.class);			
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_TITLE, getText(R.string.example_three));
			item.put(ITEM_SUBTITLE, getText(R.string.example_three_subtitle));
			data.add(item);
			activityMapping.put(i++, ExampleThreeActivity.class);			
		}
		
		{
			final Map<String, Object> item = new HashMap<String, Object>();
			item.put(ITEM_TITLE, getText(R.string.example_four));
			item.put(ITEM_SUBTITLE, getText(R.string.example_four_subtitle));
			data.add(item);
			activityMapping.put(i++, ExampleFourActivity.class);			
		}
		
		final SimpleAdapter dataAdapter = new SimpleAdapter(this, data, R.layout.toc_item, new String[] {ITEM_IMAGE, ITEM_TITLE, ITEM_SUBTITLE}, new int[] {R.id.Image, R.id.Title, R.id.SubTitle});
		setListAdapter(dataAdapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			 public void onItemClick(AdapterView<?> parent, View view,
				        int position, long id) 
			{
				final Class<? extends Activity> activityToLaunch = activityMapping.get(position);
				
				if (activityToLaunch != null)
				{
					final Intent launchIntent = new Intent(MainActivity.this, activityToLaunch);
					startActivity(launchIntent);
				}				
			}
		});
	}
}
