package itu.malta.drunkendroid.domain;

import java.util.*;

public class Trip {
	public List<Event> events = new ArrayList<Event>();
	public Long startDate = null;
	public Long localId = null;
	public Long remoteId = null;
	
	//TODO Make a comment in the report concerning optimization by excluding getter/setter!
	
	public static List<Event> filterEvents(List<Event> events, Set<String> filter) {
		List<Event> filteredEvents = new ArrayList<Event>();
		
		for(Event e : events) {
			if(filter.contains(e.getClass().getName())) {
				filteredEvents.add(e);
			}
		}
		
		return filteredEvents;
	}
}
