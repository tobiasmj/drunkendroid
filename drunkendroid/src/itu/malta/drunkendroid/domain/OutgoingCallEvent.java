package itu.malta.drunkendroid.domain;

public class OutgoingCallEvent extends CallEvent {
	
	public OutgoingCallEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, Long duration) {
		super(datetime,latitude,longitude,phonebookName,phonenumber,duration);
	}
}
