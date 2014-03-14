package com.example.drexelcabapp;

import com.cloudmine.api.SimpleCMObject;

import android.os.Bundle;
import android.app.Activity;
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

public class ReviewMainActivity extends Activity {
	private RatingBar ratingBar;
	private EditText review;
	private Button submit;
	private float stars;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_main);
		// Show the Up button in the action bar.
		setupActionBar();

		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		review = (EditText) findViewById(R.id.review);
		submit = (Button) findViewById(R.id.submit);
		title = getIntent().getStringExtra("Title");

		// Rating bar listener
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				stars = rating;
				//Toast.makeText(ReviewMainActivity.this, String.valueOf(stars),
					//	Toast.LENGTH_SHORT).show();
			}
		});

		//Submit to cloudmine the rating,review, and event id
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String rating = String.valueOf(stars);
				String text = review.getText().toString();
				SimpleCMObject review = new SimpleCMObject();
				review.add("Title", title);
				review.add("Rating", rating);
				review.add("Review", text);
				review.save();
				Toast.makeText(ReviewMainActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}// end onCreate

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.review_main, menu);
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
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
