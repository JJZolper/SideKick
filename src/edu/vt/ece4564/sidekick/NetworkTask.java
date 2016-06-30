package edu.vt.ece4564.sidekick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.location.Location;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

/*
 * Input Parameters:
 * 	[0]: Servlet - /locations /places /reviews
 * 	-/locations
 * 		[1]: Place name
 * 	-/places
 * 		[1]: Latitude
 * 		[2]: Longitude
 * 	-/reviews
 * 		[1]: Place name
 */

// Example call:	Collin:		mNetTask.execute("/places", Double.toString(mLocation.getLatitude()),
//													Double.toString(mLocation.getLongitude()));
//					JJ:			mNetTask.execute("/locations", mPlaceName);
//					Anamitra:	mNetTask.execute("/reviews", mPlaceName);
public class NetworkTask extends AsyncTask<String, Void, Integer> {
	private NetworkTaskListener mListener;
	private ArrayList<Place> mPlaces;
	private Place mPlaceInfo;
	private String mURL = "http://54.201.102.240:8080", mServlet;
	private String mReviewString;

	public NetworkTask(NetworkTaskListener listener) {
		mPlaces = new ArrayList<Place>();
		mListener = listener;
	}

	@Override
	protected Integer doInBackground(String... params) {
		int status = HttpStatus.SC_NOT_FOUND;
		
		mServlet = params[0];
		
		if(!(mServlet.equals("/locations") || mServlet.equals("/places") || mServlet.equals("/reviews")))
			return status;
		
		
		// Setup network connection
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(mURL + mServlet);
		

		// Set up key value pairs for the post
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if(mServlet.equals("/places")) {
			nameValuePairs.add(new BasicNameValuePair("lat", params[1]));
			nameValuePairs.add(new BasicNameValuePair("lon", params[2]));
		} else if(mServlet.equals("/locations")) {
			nameValuePairs.add(new BasicNameValuePair("name", params[1]));
		} else if(mServlet.equals("/reviews")) {
			nameValuePairs.add(new BasicNameValuePair("name", params[1]));
		}
		

		try {

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Make a 10 second network timeout
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			post.setParams(httpParams);

			// Communicate
			HttpResponse response = client.execute(post);
			status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				if(mServlet.equals("/places")) {
					parsePlaces(response);
				} else if(mServlet.equals("/locations")) {
					parseLocations(response);
				} else if(mServlet.equals("/reviews")) {
//					parseReviews(response);
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							response.getEntity().getContent(), "UTF-8"));
					StringBuilder builder = new StringBuilder();
					for (String line = null; (line = reader.readLine()) != null;) {
						builder.append(line).append("\n");
					}
					mReviewString = builder.toString();

				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	@Override
	protected void onPostExecute (Integer status){
		super.onPostExecute(status);
		switch (status) {
		case HttpStatus.SC_OK:
			if(mServlet.equals("/places")) {
				mListener.loadPlaces(mPlaces);
			} else if(mServlet.equals("/locations")) {
				mListener.loadLocations(mPlaceInfo);
			} else if(mServlet.equals("/reviews")) {
//				mListener.loadReviews(mPlaces);
				mListener.loadReviews(mReviewString);
			}
			break;
		default:
			mListener.onNetworkFail();
			break;
		}
	}

	private void parsePlaces(HttpResponse response) throws IOException{
		Place place;
		String key;
		Location loc;

		JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
		Log.i("Network Places JSON", reader.toString());
		
		//Parse JSON from response
		reader.beginArray();
		while(reader.hasNext()) {
			place = new Place();
			loc = new Location("gps");
			
			reader.beginObject();
			while(reader.hasNext()) {
				key = reader.nextName();
				if (key.equals("Name")) {
					place.setName(reader.nextString());
				} else if (key.equals("Lat")) {
					loc.setLatitude(reader.nextDouble());
				} else if (key.equals("Lon")) {
					loc.setLongitude(reader.nextDouble());
				} else if (key.equals("Reviews")) {
					
					reader.beginArray();
					while(reader.hasNext()) {
						place.addReview(reader.nextString());
					}
					reader.endArray();
					
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			
			place.setLocation(loc);
			mPlaces.add(place);
		}
		reader.endArray();
		reader.close();
	}
	
	private void parseLocations(HttpResponse response) throws IOException {

		JsonReader reader = new JsonReader(new InputStreamReader(response
				.getEntity().getContent()));
		Log.i("Network Places JSON", reader.toString());

		Place place = new Place();
		String key;

		// Parse JSON from response
		reader.beginObject();
		while (reader.hasNext()) {
			key = reader.nextName();
			if (key.equals("Name")) {
				place.setName(reader.nextString());
			} else if (key.equals("Number")) {
				place.setPhoneNumber(reader.nextString());
			} else if (key.equals("Desc")) {
				place.setDescription(reader.nextString());
			} else if (key.equals("EventName")) {
				place.setEventName(reader.nextString());
			} else if (key.equals("EventTime")) {
				place.setEventTime(reader.nextString());
			} else if (key.equals("EventDesc")) {
				place.setEventDescription(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();

		mPlaceInfo = place;

		reader.close();

	}
	
	private void parseReviews(HttpResponse response) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));
		Log.i("Network Places JSON", reader.toString());

		//TODO: Anamitra: Code json parser here
		// Store your stuff in mPlaces
		
		reader.close();
	}
}
