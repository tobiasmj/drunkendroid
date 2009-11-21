package itu.malta.drunkendroid.ui.activities;

import java.util.ArrayList;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.ui.map.MoodOverlay;

import android.location.Location;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class ViewTripActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	MoodOverlay _moodOverlay;
	GeoPoint _gp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    	
        _mapView = (MapView)findViewById(R.id.mapView);
        _mapView.setBuiltInZoomControls(true);
        
        _mapController = _mapView.getController();
        
        if(getIntent().getExtras() != null)
        {
        	long startTime = getIntent().getExtras().getLong("startTime");
            TripRepository tripRep = new TripRepository(this);
            Trip trip = tripRep.getTrip(startTime);
            tripRep.closeRepository();
            
        	Double minLat = null;
        	Double maxLat = null;
        	Double minLong = null;
        	Double maxLong = null;
        	ArrayList<LocationEvent> locationEvents = new ArrayList<LocationEvent>();
            
            for(Event e : trip.getTripEvents())
            {
            	// Set the min and max latitude and longitude
            	if(e.latitude != null && e.longitude != null)
            	{
            		if(minLat > e.latitude)
            			minLat = e.latitude;
            		else if(maxLat < e.latitude)
            			maxLat = e.latitude;
            		
            		if(minLong > e.longitude)
            			minLong = e.longitude;
            		else if(maxLong < e.longitude)
            			maxLong = e.longitude;
            		
            	}
            	
            	if(LocationEvent.class.isInstance(e))
            		locationEvents.add((LocationEvent)e);
            	
            	
            }
            
            float[] span = null;
            Location.distanceBetween(minLat, minLong, maxLat, maxLong, span);

            _mapController.zoomToSpan((int)(span[0]*1E6), (int)(span[1]*1E6));
            
        }
        else
        	finish();
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
