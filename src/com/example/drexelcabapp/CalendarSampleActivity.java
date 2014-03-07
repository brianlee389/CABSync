/*package com.example.drexelcabapp;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
//import com.google.api.services.calendar.model.Calendar;

import com.google.api.services.calendar.model.Event;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class CalendarSampleActivity extends Activity {

  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String PREF_ACCOUNT_NAME = "accountName";

  static final String TAG = "CalendarSampleActivity";

  static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

  static final int REQUEST_AUTHORIZATION = 1;

  static final int REQUEST_ACCOUNT_PICKER = 2;

  final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

  GoogleAccountCredential credential;

  public ArrayAdapter<Event> adapter;

  com.google.api.services.calendar.Calendar client;

  int numAsyncTasks;

  private ListView listView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMMMM dd, EEEEEEEE");
    Toast.makeText(this, DateTime.now().toString(formatter), Toast.LENGTH_SHORT).show();
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
        
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    // view and menu
    setContentView(R.id.list);
    listView = (ListView) findViewById(R.id.list);
    listView.setAdapter(adapter);
    adapter.setNotifyOnChange(true);
    // Google Accounts
    credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    // Calendar client
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("sound-proposal-476")
        .build();
    
  }//end onCreate

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
            connectionStatusCode, CalendarSampleActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }//end showGooglePlayServices

  void refreshView(List<Event> events) {
	 /* for(int i = 0; i < events.size(); i++){
		  if(events.get(i).getStart().getDate() == null){
			  events.remove(i);
		  }
	  } */
/*	  Collections.sort(events, new EventComparer());
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.cabmain, menu);
    return super.onCreateOptionsMenu(menu);
  }
/*
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
  }*/

  /** Check that Google Play services APK is installed and up to date. 
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
} */