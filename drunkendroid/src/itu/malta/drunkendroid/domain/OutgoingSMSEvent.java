package itu.malta.drunkendroid.domain;

public class OutgoingSMSEvent extends SMSEvent {
	
	public OutgoingSMSEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, String textMessage) {
		super(datetime,latitude,longitude,phonebookName,phonenumber,textMessage);
	}
}
