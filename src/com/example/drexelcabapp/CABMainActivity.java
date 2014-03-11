package com.example.drexelcabapp;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;

import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.app.Dialog;



public class CABMainActivity extends Activity {
	public ArrayAdapter<Event> adapter;
	private ListView listView;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	static final int REQUEST_AUTHORIZATION = 1;
	static final int REQUEST_ACCOUNT_PICKER = 2;
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
	GoogleAccountCredential credential;
	com.google.api.services.calendar.Calendar client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cabmain);
		//create ListView
		listView = (ListView) findViewById(R.id.list);			
						
		//Array Adapter    
	    adapter = new ArrayAdapter<Event>(this, android.R.layout.simple_list_item_1) {

	        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMMMM dd, EEEEEEEE");

	        @Override
	      public View getView(int position, View convertView, ViewGroup parent) {
	        // by default it uses toString; override to use summary instead
	        TextView view = (TextView) super.getView(position, convertView, parent);
	        Event event = getItem(position);
	        DateTime time;
	        if(event.getStart().getDateTime() != null){
	        	 time = new DateTime(event.getStart().getDateTime().getValue());
	             view.setText(event.getSummary() + "\n" + time.toString(formatter));
	        }else{
	        	view.setText(event.getSummary());
	        }
	        return view;
	      }
	    };
	    
	    // Google Accounts
	    credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
	    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
	    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
	    // Calendar client
	    client = new com.google.api.services.calendar.Calendar.Builder(
	        transport, jsonFactory, credential).setApplicationName("sound-proposal-476")
	        .build();
				
		//Intent intent = new Intent(this,CalendarSampleActivity.class);
		//startActivity(intent);
		//CalendarSampleActivity test = new CalendarSampleActivity();
		
		listView.setAdapter(adapter);
		listView.setClickable(true);
		listView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				//On click will move to another screen to display event in detail
				//Call a new activity's onCreate for that screen
				Intent intent = new Intent(CABMainActivity.this,EventMainActivity.class);
		        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMMMM dd, EEEEEEEE kk:mm");
	        	DateTime time = new DateTime(adapter.getItem(position).getStart().getDateTime().getValue());
				
	        	intent.putExtra("Title", adapter.getItem(position).getSummary());
				intent.putExtra("Date Time", time.toString(formatter));
				if(adapter.getItem(position).getLocation() == null){

					intent.putExtra("Location", "No Location");
				}else{
					intent.putExtra("Location", adapter.getItem(position).getLocation());
				}
				if(adapter.getItem(position).getDescription() == null){
					intent.putExtra("Event Description", "No Description");
				}else{
					intent.putExtra("Event Description", adapter.getItem(position).getDescription());
				}
				startActivity(intent);
				Toast.makeText(CABMainActivity.this, "to EventMainActivity", Toast.LENGTH_LONG).show();
			}
		});
		adapter.setNotifyOnChange(true);
						
		CalendarAsyncTask  eventTask = new CalendarAsyncTask(this);
		eventTask.execute();	
	}

	void refreshView(List<Event> events) {
		for(int i = 0; i < events.size(); i++){
			if(events.get(i).getStart().getDateTime() == null){
				events.remove(i);
			}
		}
		
		  Collections.sort(events, new EventComparer());
		  adapter.clear();
		  adapter.addAll(events);
		  adapter.notifyDataSetChanged();
	}

	  @Override
	  protected void onResume() {
	    super.onResume();
	    if (checkGooglePlayServicesAvailable()) {
	      haveGooglePlayServices();
	    }
	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	} //end onCreateOptionsMenu
	
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	      case R.id.menu_refresh:
	    	  new CalendarAsyncTask(this).execute();
	    	  break;
	      case R.id.menu_accounts:
	        chooseAccount();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch (requestCode) {
	      case REQUEST_GOOGLE_PLAY_SERVICES:
	        if (resultCode == Activity.RESULT_OK) {
	          haveGooglePlayServices();
	        } else {
	          checkGooglePlayServicesAvailable();
	        }
	        break;
	      case REQUEST_AUTHORIZATION:
	        if (resultCode == Activity.RESULT_OK) {
	          new CalendarAsyncTask(this).execute();
	        } else {
	          chooseAccount();
	        }
	        break;
	      case REQUEST_ACCOUNT_PICKER:
	        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
	          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
	          if (accountName != null) {
	            credential.setSelectedAccountName(accountName);
	            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putString(PREF_ACCOUNT_NAME, accountName);
	            editor.commit();
	            new CalendarAsyncTask(this).execute();
	          }
	        }
	        break;
	    }
	  }
	
	void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
	    runOnUiThread(new Runnable() {
	      public void run() {
	        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
	            connectionStatusCode, CABMainActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
	        dialog.show();
	      }
	    });
	}

	  /** Check that Google Play services APK is installed and up to date. */ 
	  private boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
	      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	      return false;
	    }
	    return true;
	  }

	  private void haveGooglePlayServices() {
	    // check if there is already an account selected
	    if (credential.getSelectedAccountName() == null) {
	      // ask user to choose account
	      chooseAccount();
	    } else {
	      // load calendars
	        new CalendarAsyncTask(this).execute();
	    }
	  }

	  private void chooseAccount() {
	    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	  }
	
}//end class CABMainActivity
