package itu.malta.drunkendroid.domain;

import java.util.*;

public class Trip {
	private ArrayList<Event> events = new ArrayList<Event>();
	private Long _startDate = null;
	private Long _localId = null;
	private Long _remoteId = null;
	
	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}

	public Long getStartDate() {
		return _startDate;
	}

	public void setStartDate(Long startDate) {
		this._startDate = startDate;
	}

	public Long getLocalId() {
		return _localId;
	}

	public void setLocalId(Long localId) {
		this._localId = localId;
	}

	public Long getRemoteId() {
		return _remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this._remoteId = remoteId;
	}
	
	//TODO Make a comment in the report concerning optimization by excluding getter/setter!
	
	public static ArrayList<Event> filterEvents(ArrayList<Event> events,HashSet<String> filter) {
		ArrayList<Event> filteredEvents = new ArrayList<Event>();
		
		for(Event e : events) {
			if(filter.contains(e.getClass().getName())) {
				filteredEvents.add(e);
			}
		}
		
		return filteredEvents;
	}
}
