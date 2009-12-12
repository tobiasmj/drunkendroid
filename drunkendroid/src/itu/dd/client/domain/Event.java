package itu.dd.client.domain;

import android.location.Location;

public abstract class Event implements Comparable<Event> {
	private Long _dateTime;
	private Double _latitude;
	private Double _longitude;
	private int _id;
	
	public Event(Location location) {
		this._dateTime = System.currentTimeMillis();
		//If the GPS haven't got a location, don't set it, or it'll be set to 0.0
		if(location != null)
		{
			this._latitude = location.getLatitude();
			this._longitude = location.getLongitude();
		}
	}
	
	public Event(Long datetime, Double latitude, Double longitude) {
		this._dateTime = datetime;
		this._latitude = latitude;
		this._longitude = longitude;
	}
	
	public Long getDateTime() {
		return _dateTime;
	}

	public void setDateTime(Long dateTime) {
		this._dateTime = dateTime;
	}

	public Double getLatitude() {
		return _latitude;
	}

	public void setLatitude(Double latitude) {
		this._latitude = latitude;
	}

	public Double getLongitude() {
		return _longitude;
	}

	public void setLongitude(Double longitude) {
		this._longitude = longitude;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}
	
	public int compareTo(Event event) {
		if(this._dateTime < event.getDateTime())
			return -1;
		else if(this._dateTime > event.getDateTime())
			return 1;
		else
			return 0;
	}


}
