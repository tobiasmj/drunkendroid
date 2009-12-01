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
	private LocationManager manager;
	private LocationListener locationListener;
	private Location lastKnownLocation;
	private ArrayList<ILocationAdapterListener> listeners = new ArrayList<ILocationAdapterListener>();
	private String provider;
	private int time = 60000;
	private int distance = 10;
	
	public GPSLocationAdapter(Context context)
	{
		System.out.println("GPSLocationAdapter created");
		this.manager = (LocationManager)context.getSystemService(android.content.Context.LOCATION_SERVICE);
		this.locationListener = new LocationListener()
    	{
    		public void onLocationChanged(Location location)
    		{
    			System.out.println("GPSLocationAdapter onLocationChanged..");
    			lastKnownLocation = location;
    			for(ILocationAdapterListener i : listeners)
    				i.OnLocationChange(lastKnownLocation);
    		}
    		public void onProviderDisabled(String provider)	{}
    		public void onProviderEnabled(String provider) {}
    		public void onStatusChanged(String provider, int status, Bundle extras) {}
    	};
    	
		provider = manager.getBestProvider(GetCriteria(), true);
		lastKnownLocation = new Location(provider);
		lastKnownLocation.setTime(Calendar.getInstance().getTimeInMillis());
		
		OutdateLocation();
		
		Connect();
	}
	
	public void Connect() {
		// Request location updates
    	manager.requestLocationUpdates(provider, time, distance, locationListener);
    	
	}
	
	public void Disconnect() {
		manager.removeUpdates(locationListener);
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
		return this.lastKnownLocation;
	}
	
	public void RegisterLocationUpdates(ILocationAdapterListener interest) {
		boolean found = false;
		for(ILocationAdapterListener i : listeners) {
			if (i == interest) {
				found = true; break;
			}
		}
		if(!found)
			listeners.add(interest);
	}
	
	public void UnregisterLocationUpdates(ILocationAdapterListener interest) {
		for(int i = 0; i<listeners.size(); i++) {
			if(listeners.get(i) == interest) {
				listeners.remove(i);
				break;
			}
		}
		if(listeners.size() == 0)
			Disconnect();
	}

	public void OutdateLocation() {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isOutdated", true);
		lastKnownLocation.setExtras(bundle);
	}
}
