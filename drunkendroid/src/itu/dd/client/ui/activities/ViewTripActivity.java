package itu.dd.client.ui.activities;

import java.util.ArrayList;
import java.util.List;

import itu.dd.client.control.TripRepository;
import itu.dd.client.domain.CallEvent;
import itu.dd.client.domain.Event;
import itu.dd.client.domain.LocationEvent;
import itu.dd.client.domain.MoodEvent;
import itu.dd.client.domain.SMSEvent;
import itu.dd.client.domain.Trip;
import itu.dd.client.ui.map.CallEventsOverlay;
import itu.dd.client.ui.map.LocationEventsOverlay;
import itu.dd.client.ui.map.MoodEventsOverlay;
import itu.dd.client.ui.map.MoodOverlay;
import itu.dd.client.ui.map.SMSEventsOverlay;
import itu.dd.client.R;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Shows a map of a trip and related events.
 */
public class ViewTripActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	MoodOverlay _moodOverlay;
	GeoPoint _gp;
    ArrayList<LocationEvent> _locationEvents = new ArrayList<LocationEvent>();
    ArrayList<MoodEvent> _moodEvents = new ArrayList<MoodEvent>();
    ArrayList<CallEvent> _callEvents = new ArrayList<CallEvent>();
    ArrayList<SMSEvent> _smsEvents = new ArrayList<SMSEvent>();
	ArrayList<Event> _otherEvents = new ArrayList<Event>();
	
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

            // The latitude is clamped between -180 degrees and +180 degrees
            int minLat = (int)(+81 * 1E6);
            int maxLat = (int)(-81 * 1E6);
            
            // The longitude is clamped between -180 degrees and +180 degrees
            int minLong = (int)(+181 * 1E6);
            int maxLong = (int)(-181 * 1E6);
            
        	ArrayList<Event> events = trip.getEvents();
        	
            for(Event e : events)
            {
            	// Set the min and max latitude and longitude
        		int latitude = (int)(e.getLatitude() * 1E6);
                int longitude = (int)(e.getLongitude() * 1E6); 
        		
        		if(minLat > latitude) minLat = latitude;
        		else if(maxLat < latitude) maxLat = latitude;
        		
        		if(minLong > longitude) minLong = longitude;
        		else if(maxLong < longitude) maxLong = longitude;
            	
            	// Add events to appropriate arraylist
            	if(LocationEvent.class.isInstance(e))
            		_locationEvents.add((LocationEvent)e);
            	else if(MoodEvent.class.isInstance(e))
            		_moodEvents.add((MoodEvent)e);
            	else if(CallEvent.class.isInstance(e))
            		_callEvents.add((CallEvent)e);
            	else if(SMSEvent.class.isInstance(e))
            		_smsEvents.add((SMSEvent)e);
                else 
            		_otherEvents.add(e);
            }
            // Zoom to span from the list of points
            _mapController.zoomToSpan(
                      (maxLat - minLat),
                      (maxLong - minLong));
            
            // Animate to the center cluster of points
            GeoPoint gp = new GeoPoint((maxLat + minLat)/2,(maxLong + minLong)/2);
            
            _mapController.animateTo(gp, new Runnable() {
    	        public void run()
    	        {
    	        	List<Overlay> overlays = _mapView.getOverlays();

    	            // Add overlay for callEvents
    	            CallEventsOverlay callOverlay = new CallEventsOverlay(_callEvents);
    	            overlays.add(callOverlay);

    	            // Add overlay for locationEvents
    	            LocationEventsOverlay locationOverlay = new LocationEventsOverlay(_locationEvents);
    	            overlays.add(locationOverlay);

    	            // Add overlay for moodEvents
    	            MoodEventsOverlay moodOverlay = new MoodEventsOverlay(_moodEvents);
    	            overlays.add(moodOverlay);

    	            // Add overlay for smsEvents
    	            SMSEventsOverlay smsOverlay = new SMSEventsOverlay(_smsEvents);
    	            overlays.add(smsOverlay);
    	        }
            });
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