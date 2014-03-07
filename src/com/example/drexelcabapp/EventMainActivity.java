package com.example.drexelcabapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;

public class EventMainActivity extends Activity {
	private Button addEvent, submit;
	private RatingBar ratingBar;
	private EditText review;
	private float stars;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_main);
		// Show the Up button in the action bar.
		setupActionBar();
		
		addEvent = (Button) findViewById(R.id.addEvent);
		submit = (Button) findViewById(R.id.submitButton);
		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		review = (EditText) findViewById(R.id.review);
		
		//Rating bar listener
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
					stars = rating;
					Toast.makeText(EventMainActivity.this, String.valueOf(stars), Toast.LENGTH_SHORT).show();
				}
			});
		
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				String text = review.getText().toString();
				Toast.makeText(EventMainActivity.this, text, Toast.LENGTH_SHORT).show();
			}
		}); 
		
		addEvent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Toast.makeText(EventMainActivity.this, "You added event to calendar", Toast.LENGTH_SHORT).show();
				createNotification(v);
			}
		}); 
	}//end onCreate

	public void createNotification(View view) {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    //Intent intent = new Intent(this,EventMainActivity.class);
	    //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

	    // Build notification
	    NotificationCompat.Builder mBuilder =
	    	    new NotificationCompat.Builder(this)
	    	    .setSmallIcon(R.drawable.ic_launcher)
	    	    .setContentTitle("My notification")
	    	    .setContentText("Hello World!")
	    	    .setAutoCancel(true);
	    		
	    
	    int mNotificationId = 001;
	 // Gets an instance of the NotificationManager service
	 NotificationManager mNotifyMgr = 
	         (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	 // Builds the notification and issues it.
	 mNotifyMgr.notify(mNotificationId, mBuilder.build());
	  }//end createNotification
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
