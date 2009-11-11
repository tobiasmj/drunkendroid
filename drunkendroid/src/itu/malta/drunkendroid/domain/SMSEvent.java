package itu.malta.drunkendroid.domain;

public class SMSEvent extends Event {
	private String phonebookName;
	private String phonenumber;
	private String textMessage;
	
	public SMSEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, String textMessage) {
		super(datetime,latitude,longitude);
	}
	
	public String getPhonebookName() {
		return phonebookName;
	}
	
	public void setPhonebookName(String phonebookName) {
		this.phonebookName = phonebookName;
	}
	
	public String getPhonenumber() {
		return phonenumber;
	}
	
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	
	public String getTextMessage() {
		return textMessage;
	}
	
	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}
}
