package com.example.drexelcabapp;

import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;

import java.io.IOException;

/**
 * Asynchronously load the calendars.
 * 
 * @author Yaniv Inbar
 */
class AsyncLoadCalendars extends CalendarAsyncTask {

  AsyncLoadCalendars(CalendarSampleActivity calendarSample) {
    super(calendarSample);
  }

  @Override
  protected void doInBackground() throws IOException {
    com.google.api.services.calendar.model.Events feed = client.events().list("7s5ln95p2m87ktqcql07ige7j4@group.calendar.google.com").execute();
    model.reset(feed.getItems());
  }

  static void run(CalendarSampleActivity calendarSample) {
    new AsyncLoadCalendars(calendarSample).execute();
  }
}
