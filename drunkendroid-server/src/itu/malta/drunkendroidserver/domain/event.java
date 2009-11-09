package itu.malta.drunkendroidserver.domain;

public abstract class event {

	String type;
	double longitude, latitude;
	
	public event(String type, double longitude, double latitude) {
		super();
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLonitude(double lonitude) {
		this.longitude = lonitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
