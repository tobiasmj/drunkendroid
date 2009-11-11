package itu.malta.drunkendroid.domain;

public class IncomingCallEvent extends CallEvent {
	
	public IncomingCallEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, Long duration) {
		super(datetime,latitude,longitude,phonebookName,phonenumber,duration);
	}
}
