package itu.dd.client.control.services;

import itu.dd.client.R;

import java.util.ArrayList;
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
	private Location _lastKnownLocation = null;
	private ArrayList<ILocationAdapterListener> _listeners = new ArrayList<ILocationAdapterListener>();
	private String _provider;
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

		_provider = _manager.getBestProvider(GetCriteria(), true);

		Connect();
	}

	/**
	 * When a location update is received from the GPS, all the interested
	 * listeners are called with the location as parameter.
	 * 
	 * @param location The newly changed location.
	 */
	private void receiveUpdate(Location location) {
		Toast t = Toast.makeText(DrunkenService.getInstance(),
				"GPS update, Accuracy:" + location.getAccuracy(), 8);
		t.show();

		// Set the accuracy of the GPS to the amount set in the application
		// settings.
		String[] GPSArray = _context.getResources().getStringArray(
				R.array.gps_accuracy_options);
		SharedPreferences sp = _context.getSharedPreferences("prefs_config",
				Context.MODE_PRIVATE);
		int selectedIndex = sp.getInt("GPSAccuracy", 2);

		_lastKnownLocation = location;
		if (location.getAccuracy() < Integer.parseInt(GPSArray[selectedIndex])) {
			for (ILocationAdapterListener i : _listeners)
				i.OnLocationChange(_lastKnownLocation);
		}
	}

	/**
	 * Start listening for changes on the GPS.
	 */
	public void Connect() {
		// Request location updates
		_manager.requestLocationUpdates(_provider, _time, _distance,
				_locationListener);
	}

	/**
	 * Stop listening for changes on the GPS.
	 */
	public void Disconnect() {
		_manager.removeUpdates(_locationListener);
	}

	/**
	 * Sets a criteria for when the GPS should return updates.
	 */
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

	/**
	 * Returns the GPS' last known location.
	 * 
	 * @return A location object describing the GPS' last known location.
	 */
	public Location GetLastKnownLocation() {
		return this._lastKnownLocation;
	}

	/**
	 * Adds a listener to the Location Adapter. If the listener has already been
	 * added, nothing will happen.
	 */
	public void RegisterLocationUpdates(ILocationAdapterListener interest) {
		boolean found = false;
		for (ILocationAdapterListener i : _listeners) {
			if (i == interest) {
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
	public void UnregisterLocationUpdates(ILocationAdapterListener interest) {
		for (int i = 0; i < _listeners.size(); i++) {
			if (_listeners.get(i) == interest) {
				_listeners.remove(i);
				break;
			}
		}
		if (_listeners.size() == 0)
			Disconnect();
	}
}
