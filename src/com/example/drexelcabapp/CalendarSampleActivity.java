package com.example.drexelcabapp;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
//import com.google.api.services.calendar.model.Calendar;

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

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

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

  CalendarModel model = new CalendarModel();

  ArrayAdapter<CalendarInfo> adapter;

  com.google.api.services.calendar.Calendar client;

  int numAsyncTasks;

  private ListView listView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // enable logging
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    // view and menu
    setContentView(R.layout.calendarlist);
    listView = (ListView) findViewById(R.id.list);
    registerForContextMenu(listView);
    // Google Accounts
    credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    // Calendar client
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("sound-proposal-476")
        .build();
  }

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
            connectionStatusCode, CalendarSampleActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }

  void refreshView() {
    adapter = new ArrayAdapter<CalendarInfo>(
        this, android.R.layout.simple_list_item_1, model.toSortedArray()) {

        @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        // by default it uses toString; override to use summary instead
        TextView view = (TextView) super.getView(position, convertView, parent);
        CalendarInfo calendarInfo = getItem(position);
        view.setText(calendarInfo.summary);
        return view;
      }
    };
    listView.setAdapter(adapter);
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
          AsyncLoadCalendars.run(this);
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
            AsyncLoadCalendars.run(this);
          }
        }
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_refresh:
        AsyncLoadCalendars.run(this);
        break;
      case R.id.menu_accounts:
        chooseAccount();
        return true;
    }
    return super.onOptionsItemSelected(item);
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
      AsyncLoadCalendars.run(this);
    }
  }

  private void chooseAccount() {
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
  }
}
