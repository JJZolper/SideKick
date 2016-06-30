package edu.vt.ece4564.sidekick;

import java.util.ArrayList;
import java.util.List;

import vt.edu.ece4564.sidekick.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;


public class CameraView extends Activity {
    private NetworkTask mNetTask;
    private Location mLocation;
    private ArrayList<Place> mPlaces;
    private LocationManager mLm;
    private LocationListener mLocListener;
    private SensorEventListener mSensorListener;
    
    private ExpandableListView mListView;
	private FrameLayout mFrame;
	private CameraSurface mSurface;
	private SensorManager mSensorManager;
	
	private CameraOverlayView mOverlay;
	
	private Sensor mOrientation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_view);
		mFrame = (FrameLayout) findViewById(R.id.frameLayout);
		
		mPlaces = new ArrayList<Place>();
		
		
        // Set up sensors
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mLm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        List<String> providers = mLm.getProviders(true);
        try {
        	// Get most accurate last known location (Starts with GPS...)
        	mLocation = new Location(providers.get(providers.size()-1));
        	for (int i=providers.size()-1; i>=0; i--) {
        		mLocation.setProvider(providers.get(i));
        		mLocation.set(mLm.getLastKnownLocation(providers.get(i)));
        		if (mLocation != null) break;
        	}
        } catch(NullPointerException e) {
        	mLocation.setProvider("gps");
        	e.printStackTrace();
        }
        
        

		// Set up UI
		mOverlay = new CameraOverlayView(this, mLocation, mPlaces);
		mFrame.addView(mOverlay);

		View listOverlay = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.list_places, null);

		mListView = (ExpandableListView)listOverlay.findViewById(R.id.listPlaces);
		
		mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent i = new Intent(getApplicationContext(), ReviewsView.class);
				i.putExtra("edu.vt.ece4564.sidekick.PLACE", mPlaces.get(groupPosition).getName());
				startActivity(i);
				return true;
			}
			
		});
		
		mFrame.addView(listOverlay);
		
		refresh();

        
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setupListeners();
		
		CameraListener cl = new CameraListener() {
			@Override
			public void setCameraViewAngle(float angle) {
				mOverlay.setCameraViewAngle(angle);
			}
		};
		// Orientation sensor setup
		mSensorManager.registerListener(mSensorListener, mOrientation, SensorManager.SENSOR_DELAY_UI);
		
		// Add custom view to UI
		mSurface = new CameraSurface(getApplicationContext(), cl);
		mFrame.addView(mSurface, 0);

        // Update min distance = 2m min time = 10 sec
        mLm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 2, mLocListener);
        
        Toast.makeText(getApplicationContext(), "Have a look around!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Remove sensor listeners
		mSensorManager.unregisterListener(mSensorListener);
        mLm.removeUpdates(mLocListener);
        
        // Clear custom view
		mSurface = null;
	}
	

	

	private void setupListeners() {
		// Orientation Listeners
		mSensorListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {

				if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
					mOverlay.updateDirection((float)event.values[0]);
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		};

		// Location Listener
		mLocListener = new LocationListener() {
			GeomagneticField geoField;

			@Override
			public void onLocationChanged(Location location) {
				Log.i("CamerView location", location.toString());
				mLocation = location;

				geoField = new GeomagneticField(
						Double.valueOf(location.getLatitude()).floatValue(),
						Double.valueOf(location.getLongitude()).floatValue(),
						Double.valueOf(location.getAltitude()).floatValue(),
						System.currentTimeMillis());

				mOverlay.updateLocation(location, geoField.getDeclination());
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "Please turn on GPS", Toast.LENGTH_LONG).show();
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(i);
			}
		};
	}

    public void refresh() {
        // Stop task if running
        if (mNetTask != null)
        	mNetTask.cancel(true);

        mNetTask = new NetworkTask(new NetworkTaskListener() {

            @Override
            public void onNetworkFail() {
                Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_LONG).show();
            }

			@Override
			public void loadPlaces(ArrayList<Place> places) {
                mPlaces = places;
                mOverlay.updatePlaces(mPlaces);
        		mListView.setAdapter(new OverlayListAdapter(getApplicationContext(), mPlaces, mLocation));
			}

			@Override
			public void loadLocations(Place place) {}

			@Override
			public void loadReviews(String reviews) {}
        });

        // Start network communication
        mNetTask.execute("/places", Double.toString(mLocation.getLatitude()), Double.toString(mLocation.getLongitude()));
    }
}
