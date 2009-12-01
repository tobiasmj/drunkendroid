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

	long readingTime;
	double latitude, longitude;
	private long tripId = 0;
	private String caller;
	private String reciever;
	private long endTime;
	
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
	public Call (long readingTime, double latitude, double longitude, String caller, String reciever, long endTime) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.caller = caller;
		this.reciever = reciever;
		this.endTime = endTime;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 */
	public Call (long tripId, long readingTime, double latitude, double longitude, String caller, String reciever, long endTime) {
		this.tripId = tripId;
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.caller = caller;
		this.reciever = reciever;
		this.endTime = endTime;
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
		return "call";
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
	public String getCaller() {
		return caller;
	}
	public String getReciever() {
		return reciever;
	}
	public long getEndTime() {
		return endTime;
	}
}
