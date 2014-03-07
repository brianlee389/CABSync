package com.example.drexelcabapp;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CABMainActivity extends Activity {
	ArrayAdapter<String> adapter;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cabmain);
		//create ListView
		listView = (ListView) findViewById(R.id.list);

		//ArrayList to hold the information about CAB events
		ArrayList<String> eventsList = new ArrayList<String>() ;				
						
		//Array Adapter
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1, eventsList);
				
		//Intent intent = new Intent(this,CalendarSampleActivity.class);
		//startActivity(intent);
		//CalendarSampleActivity test = new CalendarSampleActivity();
		
		listView.setAdapter(adapter);
		listView.setClickable(true);
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int postition, long id){
				//On click will move to another screen to display event in detail
				//Call a new activity's onCreate for that screen
				Intent intent = new Intent(CABMainActivity.this,EventMainActivity.class);
				startActivity(intent);
				Toast.makeText(CABMainActivity.this, "Hello World", Toast.LENGTH_LONG).show();
			}
		});{
			
		}
		adapter.setNotifyOnChange(true);
						
		EventAsyncTask  eventTask = new EventAsyncTask();
		eventTask.execute();	
	}

	//Get Google Calendar events in the background
	public class EventAsyncTask extends AsyncTask<Void,Void,Void>{
		ArrayList<String> events;
		
		@Override
		protected Void doInBackground(Void... arg0) {
			
			try{
				events = getEvents();
			}catch (Exception e){
				e.printStackTrace();
			}
			return null;
		}//end doInBackground
		
		@Override
		protected void onPostExecute(Void arg0){
			adapter.clear();
			adapter.addAll(events);
			adapter.notifyDataSetChanged();
		}//end onPostExecute
		
	}//end EventAsyncTask
	
	private ArrayList<String> getEvents() throws Exception{
		ArrayList<String> event = new ArrayList<String>();
		event.add("test");
		//Code for getting Google Calendar events go here
		
		return event;
	}//end getEvents
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cabmain, menu);
		return true;
	}//end onCreateOptionsMenu

}//end class CABMainActivity
