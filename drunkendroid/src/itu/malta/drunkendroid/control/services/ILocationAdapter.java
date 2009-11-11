package itu.malta.drunkendroid.control.services;

import android.location.Location;

public interface ILocationAdapter {
	public Location GetLastKnownLocation();
	public void Connect();
	public void Disconnect();
}
