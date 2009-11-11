package itu.malta.drunkendroid.domain;

public class Event {
	private Long datetime;
	private Double latitude;
	private Double longitude;

	public Event(Long datetime, Double latitude, Double longitude) {
		setDate(datetime);
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public Long getDate() {
		return datetime;
	}

	public void setDate(Long datetime) {
		this.datetime = datetime;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
