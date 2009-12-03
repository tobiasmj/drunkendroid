package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;


/**
 * 
 * Class representing a Reading.
 *
 */
public class Mood implements IEvent{

	private long _readingTime;
	private double _latitude, _longitude;
	private int _mood = -1;
	private long _tripId = 0;
	
	public long getTripId() {
		return _tripId;
	}
	@Override
	public void setTripId(long tripId) {
		this._tripId = tripId;
	}
	/**
	 * Constructor
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public Mood (long readingTime, double latitude, double longitude, int mood ) {
		this._readingTime = readingTime;
		this._latitude = latitude;
		this._longitude = longitude;
		this._mood = mood;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public Mood (long tripId, long readingTime, double latitude, double longitude, int mood ) {
		this._tripId = tripId;
		this._readingTime = readingTime;
		this._latitude = latitude;
		this._longitude = longitude;
		this._mood = mood;
	}
	public String getEventType() {
		return "reading";
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}
	public int getMood() {
		return _mood;
	}

	@Override
	public long getTimeStamp() {
		return _readingTime;
	}

	@Override
	public String getType() {
		return "reading";
	}

	@Override
	public void setLatitude(double latitude) {
		this._latitude = latitude;
	}

	@Override
	public void setLongitude(double longitude) {
		this._longitude = longitude;
		
	}

	@Override
	public void setTimeStamp(long timeStamp) {
		_readingTime = timeStamp;
		
	}
}
