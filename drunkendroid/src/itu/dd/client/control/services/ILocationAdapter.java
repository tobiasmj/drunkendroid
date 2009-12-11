package itu.dd.client.control.services;

import android.location.Location;

public interface ILocationAdapter {
	public Location getLastKnownLocation();
	public void connect();
	public void disconnect();
	public void registerLocationUpdates(ILocationAdapterListener interest);
	public void unregisterLocationUpdates(ILocationAdapterListener interest);
}
