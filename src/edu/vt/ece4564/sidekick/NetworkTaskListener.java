package edu.vt.ece4564.sidekick;

import java.util.ArrayList;

public interface NetworkTaskListener {
    void loadPlaces(ArrayList<Place> places);
    void loadLocations(Place place);
    void loadReviews(String reviews);
    void onNetworkFail();
}
