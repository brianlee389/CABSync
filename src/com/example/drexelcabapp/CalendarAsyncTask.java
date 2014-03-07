package com.example.drexelcabapp;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.calendar.model.Event;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;

public class CalendarAsyncTask extends AsyncTask<Void, Void, Boolean> {

  final CalendarSampleActivity activity;
  final com.google.api.services.calendar.Calendar client;
  List<Event> events;

  public CalendarAsyncTask(CalendarSampleActivity activity) {
    this.activity = activity;
    client = activity.client;
    events = null;
  }
  
  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
        com.google.api.services.calendar.model.Events feed = client.events().list("7s5ln95p2m87ktqcql07ige7j4@group.calendar.google.com").setTimeMin(new com.google.api.client.util.DateTime(DateTime.now().toDate())).setMaxResults(15).execute();
        if(feed.getItems() != null) events = feed.getItems();
        else return false;
        return true;
    } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
      activity.showGooglePlayServicesAvailabilityErrorDialog(
          availabilityException.getConnectionStatusCode());
    } catch (UserRecoverableAuthIOException userRecoverableException) {
      activity.startActivityForResult(
          userRecoverableException.getIntent(), CalendarSampleActivity.REQUEST_AUTHORIZATION);
    } catch (IOException e) {
		Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
	}
    return false;
  }
  
  @Override
  protected void onPostExecute(Boolean success){
	  if(success && events != null){
		  activity.refreshView(events);
	  }
  }

}