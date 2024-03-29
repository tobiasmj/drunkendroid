package itu.dd.client.domain;

import java.util.*;

public class Trip {
	private ArrayList<Event> events = new ArrayList<Event>();
	private Long _startDate = null;
	private Long _localId = null;
	private Long _remoteId = null;
	private String _name = null;
	
	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

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
		
		int length = events.size();
		for(int i = 0; i < length; i++) {
			if(filter.contains(events.get(i).getClass().getName())) {
				filteredEvents.add(events.get(i));
			}
		}	
		return filteredEvents;
	}
}
