package itu.malta.drunkendroid.domain;

public class IncomingSMSEvent extends SMSEvent {
	
	public IncomingSMSEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, String textMessage) {
		super(datetime,latitude,longitude,phonebookName,phonenumber,textMessage);
	}
}
