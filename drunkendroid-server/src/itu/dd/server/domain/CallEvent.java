package itu.dd.server.domain;

import itu.dd.server.interfaces.IEvent;


/**
 * 
 * Class representing a Call.
 *
 */

public class CallEvent implements IEvent{

	private long _timeStamp;
	private double _latitude, _longitude;
	private long _tripId = 0;
	private String _caller;
	private String _reciever;
	private long _endTime;

	public long getTripId() {
		return _tripId;
	}
	@Override
	public void setTripId(long tripId) {
		this._tripId = tripId;
	}
	/**
	 * Constructor
	 * @param timeStamp the time of the reading in unixTimeformat in miliseconds
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public CallEvent (long timeStamp, double latitude, double longitude, String caller, String reciever, long endTime) {
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._caller = caller;
		this._reciever = reciever;
		this._endTime = endTime;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param timeStamp the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public CallEvent (long tripId, long timeStamp, double latitude, double longitude, String caller, String reciever, long endTime) {
		this._tripId = tripId;
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._caller = caller;
		this._reciever = reciever;
		this._endTime = endTime;
	}
	public String getEventType() {
		return "call";
	}

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	@Override
	public long getTimeStamp() {
		return _timeStamp;
	}

	@Override
	public String getType() {
		return "call";
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
	public String getCaller() {
		return _caller;
	}
	public String getReciever() {
		return _reciever;
	}
	public long getEndTime() {
		return _endTime;
	}
}
