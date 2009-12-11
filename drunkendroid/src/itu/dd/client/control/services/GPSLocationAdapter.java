package itu.dd.client.control.services;

import itu.dd.client.R;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSLocationAdapter implements ILocationAdapter {
	private LocationManager _manager;
	private LocationListener _locationListener;
	private Location _lastKnownLocation = null;
	private ArrayList<ILocationAdapterListener> _listeners = new ArrayList<ILocationAdapterListener>();
	private String _provider = LocationManager.GPS_PROVIDER;
	private Context _context;
	private int _time = 60000;
	private int _distance = 10;

	public GPSLocationAdapter(Context context) {
		_context = context;
		this._manager = (LocationManager) context
				.getSystemService(android.content.Context.LOCATION_SERVICE);
		this._locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				receiveUpdate(location);
			}

			public void onProviderDisabled(String provider) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}
		};

		connect();
	}

	/**
	 * When a location update is received from the GPS, all the interested
	 * listeners are called with the location as parameter.
	 * 
	 * @param location The newly changed location.
	 */
	private void receiveUpdate(Location location) {
		// Set the accuracy of the GPS to the amount set in the application
		// settings.
		String[] GPSArray = _context.getResources().getStringArray(
				R.array.gps_accuracy_options);
		SharedPreferences sp = _context.getSharedPreferences("prefs_config",
				Context.MODE_PRIVATE);
		int selectedIndex = sp.getInt("GPSAccuracy", 2);

		_lastKnownLocation = location;
		if ((location.getAccuracy() != 0.0) && (location.getAccuracy() < Integer.parseInt(GPSArray[selectedIndex]))) {
			Location l = _lastKnownLocation;
			int length = _listeners.size();
			for(int i = 0; i < length; i++)
				_listeners.get(i).OnLocationChange(l);
		}
	}

	/**
	 * Start listening for changes on the GPS.
	 */
	public void connect() {
		// Request location updates
		_manager.requestLocationUpdates(_provider, _time, _distance,
				_locationListener);
	}

	/**
	 * Stop listening for changes on the GPS.
	 */
	public void disconnect() {
		_manager.removeUpdates(_locationListener);
	}

	/**
	 * Returns the GPS' last known location.
	 * 
	 * @return A location object describing the GPS' last known location.
	 */
	public Location getLastKnownLocation() {
		return this._lastKnownLocation;
	}

	/**
	 * Adds a listener to the Location Adapter. If the listener has already been
	 * added, nothing will happen.
	 */
	public void registerLocationUpdates(ILocationAdapterListener interest) {
		boolean found = false;
		int length = _listeners.size();
		for(int i = 0; i < length; i++) {
			if (_listeners.get(i) == interest) {
				found = true;
				break;
			}
		}
		if (!found)
			_listeners.add(interest);
	}

	/**
	 * Unregister a receiver of location updates. If there are no receivers
	 * left, the Location adapter will stop listening for location changes.
	 */
	public void unregisterLocationUpdates(ILocationAdapterListener interest) {
		for (int i = 0; i < _listeners.size(); i++) {
			if (_listeners.get(i) == interest) {
				_listeners.remove(i);
				break;
			}
		}
		if (_listeners.size() == 0)
			disconnect();
	}
}
