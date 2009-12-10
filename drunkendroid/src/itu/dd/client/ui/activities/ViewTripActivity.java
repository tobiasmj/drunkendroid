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
import itu.dd.client.ui.map.RouteOverlay;
import itu.dd.client.ui.map.ItemizedEventOverlay;
import itu.dd.client.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Shows a map of a trip and related events.
 */
public class ViewTripActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	GeoPoint _gp;
    ArrayList<LocationEvent> _locationEvents = new ArrayList<LocationEvent>();
	ArrayList<Event> _otherEvents = new ArrayList<Event>();
	Drawable _callDrawable;
	Drawable _smsDrawable;
	Drawable _moodDrawable;
	ItemizedEventOverlay _callOverlay;
	ItemizedEventOverlay _smsOverlay;
	ItemizedEventOverlay _moodOverlay;
	Resources _resources;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    	
        _resources = this.getResources();
        
        _mapView = (MapView)findViewById(R.id.mapView);
        _mapView.setBuiltInZoomControls(true);
        
        _mapController = _mapView.getController();

        // Get drawables
		_callDrawable = _resources.getDrawable(R.drawable.marker_call);
		_smsDrawable = _resources.getDrawable(R.drawable.marker_sms);
		_moodDrawable = _resources.getDrawable(R.drawable.marker_mood);
		
		_callOverlay = new ItemizedEventOverlay(_callDrawable);
		_smsOverlay = new ItemizedEventOverlay(_smsDrawable);
		_moodOverlay = new ItemizedEventOverlay(_moodDrawable);
		
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
        	
        	int length = events.size();
        	Event e;
        	for(int i = 0; i < length; i++) {
        		e = events.get(i);
            	// Set the min and max latitude and longitude
        		int latitude = (int)(e.getLatitude() * 1E6);
                int longitude = (int)(e.getLongitude() * 1E6); 
        		
        		if(minLat > latitude) minLat = latitude;
        		if(maxLat < latitude) maxLat = latitude;
        		
        		if(minLong > longitude) minLong = longitude;
        		if(maxLong < longitude) maxLong = longitude;
            	
            	// Add events to appropriate arraylist
            	if(LocationEvent.class.isInstance(e))
            		_locationEvents.add((LocationEvent)e);
            	else if(MoodEvent.class.isInstance(e))
            	{
            		GeoPoint point = new GeoPoint(latitude,longitude);
            		OverlayItem overlayitem = new OverlayItem(point, "", "");
            		_moodOverlay.addOverlay(overlayitem);
            	}
            	else if(CallEvent.class.isInstance(e))
            	{
            		GeoPoint point = new GeoPoint(latitude,longitude);
            		OverlayItem overlayitem = new OverlayItem(point, "", "");
            		_callOverlay.addOverlay(overlayitem);
            	}
            	else if(SMSEvent.class.isInstance(e))
            	{
            		GeoPoint point = new GeoPoint(latitude,longitude);
            		OverlayItem overlayitem = new OverlayItem(point, "", "");
            		_smsOverlay.addOverlay(overlayitem);
            	}
                else 
            		_otherEvents.add(e);
            }
            // Zoom to span from the list of points
            int latSpan = maxLat - minLat;
            int longSpan = maxLong - minLong;
            
            _mapController.zoomToSpan(
            		latSpan,
            		longSpan);
            
            // Animate to the center cluster of points
            GeoPoint gp = new GeoPoint((maxLat + minLat)/2,(maxLong + minLong)/2);
            
            _mapController.animateTo(gp, new Runnable() {
    	        public void run()
    	        {
    	        	List<Overlay> overlays = _mapView.getOverlays();

    	            // Add overlay for locationEvents
    	            RouteOverlay locationOverlay = new RouteOverlay(_resources, _locationEvents);
    	            overlays.add(locationOverlay);

    	            // Add overlay for callEvents
    	            if(_callOverlay.size() > 0)
    	            	overlays.add(_callOverlay);

    	            // Add overlay for moodEvents
    	            if(_moodOverlay.size() > 0)
        	            overlays.add(_moodOverlay);

    	            // Add overlay for smsEvents
    	            if(_smsOverlay.size() > 0)
        	            overlays.add(_smsOverlay);
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
