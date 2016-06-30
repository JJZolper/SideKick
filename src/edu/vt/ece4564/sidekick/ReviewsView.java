package edu.vt.ece4564.sidekick;

import java.util.ArrayList;

import vt.edu.ece4564.sidekick.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewsView extends Activity {
	private String mPlace;
	private TextView showReviews_;
	private NetworkTask nNetTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reviews);
		showReviews_ = (TextView) findViewById(R.id.Reviews);
		
		Intent i = getIntent();
        mPlace = i.getStringExtra("edu.vt.ece4564.sidekick.PLACE");
        setTitle(mPlace + " Reviews");
        
        Button btn = (Button) findViewById(R.id.toPlace);
        btn.setText("Go to " + mPlace);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), PlaceView.class);
				i.putExtra("edu.vt.ece4564.sidekick.PLACE", mPlace);
				startActivity(i);
			}
		});
        
        if (nNetTask != null)
			nNetTask.cancel(true);
        
		nNetTask = new NetworkTask(new NetworkTaskListener() {

			@Override
			public void onNetworkFail() {
				Toast.makeText(getApplicationContext(),
						"Cannot connect to server", Toast.LENGTH_LONG).show();
			}

			@Override
			public void loadPlaces(ArrayList<Place> places) {
			}

			@Override
			public void loadLocations(Place place) {
			}

			@Override
			public void loadReviews(String reviews) {
				showReviews_.setText(reviews);
			}
		});

		// Start network communication
		nNetTask.execute("/reviews", mPlace);
        
	}
}
