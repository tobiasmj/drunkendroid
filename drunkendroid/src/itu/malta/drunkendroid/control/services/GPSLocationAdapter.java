package itu.malta.drunkendroid.control.services;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSLocationAdapter implements ILocationAdapter {
	private LocationManager _manager;
	private LocationListener _locationListener;
	private Location _lastKnownLocation;
	private ArrayList<ILocationAdapterListener> _listeners = new ArrayList<ILocationAdapterListener>();
	private String _provider;
	private int _time = 60000;
	private int _distance = 10;
	private int _minAccuracy = 20;
	
	public GPSLocationAdapter(Context context)
	{
		System.out.println("GPSLocationAdapter created");
		this._manager = (LocationManager)context.getSystemService(android.content.Context.LOCATION_SERVICE);
		this._locationListener = new LocationListener()
    	{
    		public void onLocationChanged(Location location)
    		{
    			System.out.println("GPSLocationAdapter onLocationChanged..");
    			_lastKnownLocation = location;
    			if(location.getAccuracy() < _minAccuracy) {
    				for(ILocationAdapterListener i : _listeners)
        				i.OnLocationChange(_lastKnownLocation);
    			}
    		}
    		public void onProviderDisabled(String provider)	{}
    		public void onProviderEnabled(String provider) {}
    		public void onStatusChanged(String provider, int status, Bundle extras) {}
    	};
    	
		_provider = _manager.getBestProvider(GetCriteria(), true);
		_lastKnownLocation = new Location(_provider);
		_lastKnownLocation.setTime(Calendar.getInstance().getTimeInMillis());
		
		OutdateLocation();
		Connect();
	}
	
	public void Connect() {
		// Request location updates
    	_manager.requestLocationUpdates(_provider, _time, _distance, _locationListener);
    	
	}
	
	public void Disconnect() {
		_manager.removeUpdates(_locationListener);
	}
	
	private Criteria GetCriteria() {
		// Create criteria
		Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
		
    	return criteria;
	}
	
	public Location GetLastKnownLocation()
	{
		return this._lastKnownLocation;
	}
	
	public void RegisterLocationUpdates(ILocationAdapterListener interest) {
		boolean found = false;
		for(ILocationAdapterListener i : _listeners) {
			if (i == interest) {
				found = true; break;
			}
		}
		if(!found)
			_listeners.add(interest);
	}
	
	public void UnregisterLocationUpdates(ILocationAdapterListener interest) {
		for(int i = 0; i<_listeners.size(); i++) {
			if(_listeners.get(i) == interest) {
				_listeners.remove(i);
				break;
			}
		}
		if(_listeners.size() == 0)
			Disconnect();
	}

	public void OutdateLocation() {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isOutdated", true);
		_lastKnownLocation.setExtras(bundle);
	}
}
