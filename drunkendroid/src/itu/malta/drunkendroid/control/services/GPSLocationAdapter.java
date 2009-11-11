package itu.malta.drunkendroid.control.services;

import java.util.ArrayList;
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
	private ArrayList<ILocationAdapterListener> listerners;
	private String provider;
	private int time = 60000;
	private int distance = 10;
	
	public GPSLocationAdapter(Context context)
	{
		this.manager = (LocationManager)context.getSystemService(android.content.Context.LOCATION_SERVICE);
		this.locationListener = new LocationListener()
    	{
    		public void onLocationChanged(Location location)
    		{
    			lastKnownLocation = location;
    			for(ILocationAdapterListener i : listerners)
    				i.OnLocationChange(lastKnownLocation);
    		}
    		public void onProviderDisabled(String provider)	{
    			    			
    		}
    		public void onProviderEnabled(String provider) {}
    		public void onStatusChanged(String provider, int status, Bundle extras) {}
    	};
    	
		provider = manager.getBestProvider(GetCriteria(), true);
		lastKnownLocation = manager.getLastKnownLocation(provider);
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
		for(ILocationAdapterListener i : listerners) {
			if (i == interest) {
				found = true; break;
			}
		if(!found)
			listerners.add(interest);
		}
	}
	
	public void UnregisterLocationUpdates(ILocationAdapterListener interest) {
		for(int i = 0; i<listerners.size(); i++) {
			if(listerners.get(i) == interest)
				listerners.remove(i);
		}
	}
}
