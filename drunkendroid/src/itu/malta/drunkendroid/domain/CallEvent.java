package itu.malta.drunkendroid.domain;

public class CallEvent extends Event {
	private String phonebookName;
	private String phonenumber;
	private Long duration;
	
	public CallEvent(Long datetime, Double latitude, Double longitude, String phonebookName, String phonenumber, Long duration) {
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
	
	public Long getDuration() {
		return duration;
	}
	
	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
