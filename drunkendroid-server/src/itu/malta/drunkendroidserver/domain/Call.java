package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;


/**
 * 
 * Class representing a Call.
 *
 */
/*
 *    <event>
     <eventType>call</eventType>
     <dateTime>1255816133</dateTime>
     <latitude>35.908422</latitude>
     <longitude>14.502362</longitude>
     <data>
      <caller>0036577293610</caller>
      <receiver>0036577104516</receiver>
      <endTime>1255817201</endTime>
     </data
   </event>
 */
public class Call implements IEvent{

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
	public Call (long timeStamp, double latitude, double longitude, String caller, String reciever, long endTime) {
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
	public Call (long tripId, long timeStamp, double latitude, double longitude, String caller, String reciever, long endTime) {
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
