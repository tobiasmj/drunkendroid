package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;

/**
 * Class that represents a location event.
 */
public class Location implements IEvent {

	private final String type = "location";
	private double longitude, latitude;
	private Long timeStamp;
	private long tripId = 0;

	/**
	 * Constructor for the Location event.
	 * @param timeStamp the time when the event occured.
	 * @param longitude the longitude reading.
	 * @param latitude the latitude reading.
	 */
	public Location(long timeStamp, double longitude, double latitude) {
		super();
		this.timeStamp = timeStamp;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public String getType() {
		return type;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	public long getTripId() {
		return tripId;
	}
	
}
