package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;

/**
 * Class that represents a location event.
 */
public class Location implements IEvent {

	private final String _type = "location";
	private double _longitude, _latitude;
	private long _timeStamp;
	private long _tripId = 0;

	/**
	 * Constructor for the Location event.
	 * @param timeStamp the time when the event occured.
	 * @param longitude the longitude reading.
	 * @param latitude the latitude reading.
	 */
	public Location(long timeStamp, double longitude, double latitude) {
		super();
		this._timeStamp = timeStamp;
		this._longitude = longitude;
		this._latitude = latitude;
	}
	
	public String getType() {
		return _type;
	}
	public double getLongitude() {
		return _longitude;
	}
	public void setLongitude(double longitude) {
		this._longitude = longitude;
	}
	public double getLatitude() {
		return _latitude;
	}
	public void setLatitude(double latitude) {
		this._latitude = latitude;
	}
	public void setTimeStamp(long timeStamp) {
		this._timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return _timeStamp;
	}
	public void setTripId(long tripId) {
		this._tripId = tripId;
	}
	public long getTripId() {
		return _tripId;
	}
	
}
