package itu.dd.client.control.services;

import android.location.Location;

public interface ILocationAdapter {
	public Location GetLastKnownLocation();
	public void Connect();
	public void Disconnect();
	public void RegisterLocationUpdates(ILocationAdapterListener interest);
	public void UnregisterLocationUpdates(ILocationAdapterListener interest);
}
