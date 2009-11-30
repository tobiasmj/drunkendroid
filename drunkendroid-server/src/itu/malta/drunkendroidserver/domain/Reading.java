package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;


/**
 * 
 * Class representing a Reading.
 *
 */
public class Reading implements IEvent{

	long readingTime;
	double latitude, longitude;
	int mood = -1;
	private long tripId = 0;
	
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
	 * @param mood the mood valued 0-255
	 */
	public Reading (long readingTime, double latitude, double longitude, int mood ) {
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	/**
	 * Constructor 
	 * @param tripId the tripId associated with the reading
	 * @param readingTime the time of the reading in unixTimeformat
	 * @param latitude the latitude of the reading
	 * @param longitude the longitude of the reading
	 * @param mood the mood valued 0-255
	 */
	public Reading (long tripId, long readingTime, double latitude, double longitude, int mood ) {
		this.tripId = tripId;
		this.readingTime = readingTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mood = mood;
	}
	public String getEventType() {
		return "reading";
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	public int getMood() {
		return mood;
	}

	@Override
	public long getTimeStamp() {
		return readingTime;
	}

	@Override
	public String getType() {
		return "reading";
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
}
