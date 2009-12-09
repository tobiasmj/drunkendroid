package itu.dd.server.domain;

import itu.dd.server.interfaces.IEvent;


/**
 * 
 * Class representing a SMS.
 *
 */
/*
 *        <event>
     <eventType>sms</eventType>
     <dateTime>1255816133</dateTime>
     <latitude>35.908422</latitude>
     <longitude>14.502362</longitude>
     <data>
      <sender>0036577293610</sender>
      <receiver>0036577104516</receiver>
      <message>Hi, bring two bottles of gin.</message>
     </data
   </event>
 */
public class Sms implements IEvent{

	private long _timeStamp;
	private double _latitude, _longitude;
	private long _tripId = 0;
	private String _reciever;
	private String _sender;
	private String _message;
	
	public long getTripId() {
		return _tripId;
	}
	@Override
	public void setTripId(long tripId) {
		this._tripId = tripId;
	}
	/**
	 * Constructor
	 * @param timeStamp the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public Sms (long timeStamp, double latitude, double longitude, String sender, String reciever, String message) {
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._reciever = reciever;
		this._sender = sender;
		this._message = message;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param timeStamp the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public Sms (long tripId, long timeStamp, double latitude, double longitude, String sender, String reciever, String message) {
		this._tripId = tripId;
		this._timeStamp = timeStamp;
		this._latitude = latitude;
		this._longitude = longitude;
		this._reciever = reciever;
		this._sender = sender;
		this._message = message;
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
		return "SMS";
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
	public String getSender() {
		return _sender;
	}
	public String getReciever() {
		return _reciever;
	}
	public String getMessage() {
		return _message;
	}
}
