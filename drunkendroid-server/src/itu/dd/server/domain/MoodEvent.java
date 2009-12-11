package itu.dd.server.domain;

import itu.dd.server.interfaces.IEvent;


/**
 * 
 * Class representing a Mood Reading.
 *
 */
public class MoodEvent implements IEvent{

	private long _timeStamp;
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
	 * @param timeStamp the time of the reading in unixTimeformat in miliseconds.
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public MoodEvent (long timeStamp, double latitude, double longitude, int mood ) {
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._mood = mood;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param timeStamp the time of the reading in unixTimeformat in miliseconds.
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public MoodEvent (long tripId, long timeStamp, double latitude, double longitude, int mood ) {
		this._tripId = tripId;
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._mood = mood;
	}
	public String getEventType() {
		return "mood";
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
		return _timeStamp;
	}

	@Override
	public String getType() {
		return "mood";
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
		_timeStamp = timeStamp;
		
	}
}
