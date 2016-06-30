package edu.vt.ece4564.sidekick;

import java.util.ArrayList;

import android.location.Location;

public class Place {
	private String mName;
	private Location mLocation;
	private ArrayList<String> mReviews;
	private String mPhoneNumber;
	private String mDescription;
	private String mEventName;
	private String mEventTime;
	private String mEventDescription;
	
	public Place() {
		mReviews = new ArrayList<String>();
	}

	public Place(String name, Location location) {
		mName = name;
		mLocation = location;
		mReviews = new ArrayList<String>();
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setLocation(Location location) {
		mLocation = location;
	}
	
	public void addReview(String review) {
		mReviews.add(review);
	}
	
	public String getName() {
		return mName;
	}
	
	public Location getLocation() {
		return mLocation;
	}
	
	public ArrayList<String> getReviews() {
		return mReviews;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public void setDescription(String description) {
		mDescription = description;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public void setEventName(String eventName) {
		mEventName = eventName;
	}
	
	public String getEventName() {
		return mEventName;
	}
	
	public void setEventTime(String eventTime) {
		mEventTime = eventTime;
	}
	
	public String getEventTime() {
		return mEventTime;
	}

	public void setEventDescription(String eventDescription) {
		mEventDescription = eventDescription;
	}
	
	public String getEventDescription() {
		return mEventDescription;
	}

}
