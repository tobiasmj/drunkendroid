package itu.malta.drunkendroid.ui.activities;

import java.util.ArrayList;
import java.util.List;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.CallEvent;
import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.domain.SMSEvent;
import itu.malta.drunkendroid.domain.Trip;
import itu.malta.drunkendroid.ui.map.LocationEventsOverlay;
import itu.malta.drunkendroid.ui.map.MoodEventsOverlay;
import itu.malta.drunkendroid.ui.map.MoodOverlay;
import itu.malta.drunkendroid.ui.map.CallEventsOverlay;
import itu.malta.drunkendroid.ui.map.SMSEventsOverlay;

import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ViewTripActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	MoodOverlay _moodOverlay;
	GeoPoint _gp;
    ArrayList<LocationEvent> _locationEvents = new ArrayList<LocationEvent>();
    ArrayList<ReadingEvent> _moodEvents = new ArrayList<ReadingEvent>();
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
            
        	ArrayList<Event> events = trip.getTripEvents();
        	
            for(Event e : events)
            {
            	Log.i(getResources().getString(R.string.log_tag), e.latitude + "x" + e.longitude);
            	// Set the min and max latitude and longitude
            	if(e.latitude != null && e.longitude != null)
            	{
            		int latitude = (int)(e.latitude * 1E6);
                    int longitude = (int)(e.longitude * 1E6); 
            		
            		if(minLat > latitude) minLat = latitude;
            		else if(maxLat < latitude) maxLat = latitude;
            		
            		if(minLong > longitude) minLong = longitude;
            		else if(maxLong < longitude) maxLong = longitude;
            	}
            	
            	// Add events to appropriate arraylist
            	if(LocationEvent.class.isInstance(e))
            		_locationEvents.add((LocationEvent)e);
            	else if(ReadingEvent.class.isInstance(e))
            		_moodEvents.add((ReadingEvent)e);
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
