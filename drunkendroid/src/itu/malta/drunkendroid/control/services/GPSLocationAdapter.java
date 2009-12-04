package itu.malta.drunkendroid.control.services;

import itu.malta.drunkendroid.R;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class GPSLocationAdapter implements ILocationAdapter {
	private LocationManager _manager;
	private LocationListener _locationListener;
	private Location _lastKnownLocation;
	private ArrayList<ILocationAdapterListener> _listeners = new ArrayList<ILocationAdapterListener>();
	private String _provider;
	private Context _context;
	private int _time = 60000;
	private int _distance = 10;
	
	public GPSLocationAdapter(Context context)
	{
		_context = context;
		this._manager = (LocationManager)context.getSystemService(android.content.Context.LOCATION_SERVICE);
		this._locationListener = new LocationListener()
    	{
    		public void onLocationChanged(Location location)
    		{
    			Toast t = Toast.makeText(DrunkenService.getInstance(), "GPS update, Accuracy:" + location.getAccuracy(), 8);
    			t.show();
    			
    			String[] GPSArray = _context.getResources().getStringArray(
    					R.array.gps_accuracy_options);
    			SharedPreferences sp = _context.getSharedPreferences("prefs_config",
    					Context.MODE_PRIVATE);
    			int selectedIndex = sp.getInt("GPSAccuracy", 2);
    			
    			_lastKnownLocation = location;
    			if(location.getAccuracy() < Integer.parseInt(GPSArray[selectedIndex])) {
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
