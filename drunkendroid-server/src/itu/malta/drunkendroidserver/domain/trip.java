package itu.malta.drunkendroidserver.domain;

import java.util.ArrayList;

public class trip {
	String name;
	long startDateTime;
	long endDateTime;
	ArrayList<event> events = new ArrayList<event>();

	public trip(String name, long startDateTime, long endDateTime) {
		super();
		this.name = name;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(long startDateTime) {
		this.startDateTime = startDateTime;
	}

	public long getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(long endDateTime) {
		this.endDateTime = endDateTime;
	}

	public ArrayList<event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<event> events) {
		this.events = events;
	}	
}
