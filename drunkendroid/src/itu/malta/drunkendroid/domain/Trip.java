package itu.malta.drunkendroid.domain;

import java.util.*;


public class Trip {
	private ArrayList<Event> events = new ArrayList<Event>();
	private Long startDate = null;
	private Long localId;
	private Long remoteId;
	
	protected void setStartDate(Long d){
		if(this.startDate != null){
			if(this.startDate < d)
				startDate = d;
		}//If the startDate is earlier than the suggested one.
		else{
			startDate = d;
		}
	}

	public void setDateInMilliSec(long timeInMillis) {
		setStartDate(timeInMillis);
	}
	
	public Long getStartDate(){
		return this.startDate;
	}
	
	public void AddEvent(Event e) {
		events.add(e);
	}
	
	public ArrayList<Event> getTripEvents() {
		return events;
	}

	public Long getLocalID() {
		return localId;
	}
	
	public void setLocalID(Long id){
		this.localId = id;
	}

	public Long getRemoteID() {
		return remoteId;
	}
	
	public void setRemoteID(Long id){
		this.remoteId = id;
	}
}
