package edu.vt.ece4564.sidekick;

import java.util.ArrayList;
import java.util.HashMap;

import vt.edu.ece4564.sidekick.R;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class OverlayListAdapter extends BaseExpandableListAdapter {
    private ArrayList<Place> mPlaces; // header titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> mReviews;
    private Context mContext;
    private Location mLoc;

    public OverlayListAdapter(Context context, ArrayList<Place> places, Location location) {

        mPlaces = new ArrayList<Place>(places);
    	mReviews = new HashMap<String, ArrayList<String>>();
    	
    	if(!mPlaces.isEmpty()) {
	        for(Place place:mPlaces) {
	        	mReviews.put(place.getName(), place.getReviews());
	        }
    	} else {
    		mPlaces.add(new Place("No Nearby Places", location));
    		mPlaces.get(0).addReview("No Reviews");
    	}
        
        mLoc = location;

        mContext = context;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mReviews.get(mPlaces.get(groupPosition).getName()).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        TextView textView = new TextView(mContext);
        if(isLastChild)
        	textView.setPadding(32, 0, 32, 0);
        else
        	textView.setPadding(32, 0, 0, 0);
        
        textView.setText((String)getChild(groupPosition, childPosition));
        return textView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return mReviews.get(mPlaces.get(groupPosition).getName()).size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return mPlaces.get(groupPosition).getName();
    }
 
    @Override
    public int getGroupCount() {
        return mPlaces.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	
    	if(convertView == null) {
    		convertView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
    				.inflate(R.layout.list_place_item, parent, false);
    	}

        TextView name = (TextView) convertView.findViewById(R.id.placeName);
        TextView distance = (TextView) convertView.findViewById(R.id.placeDistance);

        name.setText(mPlaces.get(groupPosition).getName());
        Integer feet = ((int)(mLoc.distanceTo(mPlaces.get(groupPosition).getLocation())*3.28084));
        if(feet < 700)
        	distance.setText(feet.toString()+" ft");
        else
        	distance.setText(String.format("%.2f mi", ((double)feet)/5280));

        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
