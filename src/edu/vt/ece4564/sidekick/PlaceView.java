package edu.vt.ece4564.sidekick;

import java.util.ArrayList;

import vt.edu.ece4564.sidekick.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceView extends Activity {
	private String mPlace;
    private NetworkTask mNetTask;
    private static TextView resultingPlaceName_;
    private static TextView resultingPlacePhoneNumber_;
    private static TextView resultingPlaceDescription_;
    private static TextView resultingEventName_;
    private static TextView resultingEventTime_;
    private static TextView resultingEventDescription_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place);
		
		Intent i = getIntent();
        mPlace = i.getStringExtra("edu.vt.ece4564.sidekick.PLACE");
        setTitle(mPlace);
        
		resultingPlaceName_ = (TextView) findViewById(R.id.PlaceNameResult);
		resultingPlacePhoneNumber_ = (TextView) findViewById(R.id.PlacePhoneNumberResult);
		resultingPlaceDescription_ = (TextView) findViewById(R.id.PlaceDescriptionResult);
		resultingEventName_ = (TextView) findViewById(R.id.EventNameResult);
		resultingEventTime_ = (TextView) findViewById(R.id.EventTimeResult);
		resultingEventDescription_ = (TextView) findViewById(R.id.EventDescriptionResult);

		// Stop task if running
		if (mNetTask != null)
			mNetTask.cancel(true);

		mNetTask = new NetworkTask(new NetworkTaskListener() {

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
				// Load the Place's information on the display
				resultingPlaceName_.setText(place.getName());
				resultingPlacePhoneNumber_.setText(place.getPhoneNumber());
				resultingPlaceDescription_.setText(place.getDescription());
				resultingEventName_.setText(place.getEventName());
				resultingEventTime_.setText(place.getEventTime());
				resultingEventDescription_.setText(place.getEventDescription());
			}

			@Override
			public void loadReviews(String reviews) {
			}
		});

		// Start network communication
		mNetTask.execute("/locations", mPlace);
        
	}

}
