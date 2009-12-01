package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;


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

	long readingTime;
	double latitude, longitude;
	private long tripId = 0;
	private String reciever;
	private String sender;
	private String message;
	
	public long getTripId() {
		return tripId;
	}
	@Override
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	/**
	 * Constructor
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public Sms (long readingTime, double latitude, double longitude, String sender, String reciever, String message) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.reciever = reciever;
		this.sender = sender;
		this.message = message;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public Sms (long tripId, long readingTime, double latitude, double longitude, String sender, String reciever, String message) {
		this.tripId = tripId;
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.reciever = reciever;
		this.sender = sender;
		this.message = message;
	}
	public String getEventType() {
		return "call";
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public long getTimeStamp() {
		return readingTime;
	}

	@Override
	public String getType() {
		return "SMS";
	}

	@Override
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public void setLongitude(double longitude) {
		this.longitude = longitude;
		
	}

	@Override
	public void setTimeStamp(long timeStamp) {
		readingTime = timeStamp;
		
	}
	public String getSender() {
		return sender;
	}
	public String getReciever() {
		return reciever;
	}
	public String getMessage() {
		return message;
	}
}
