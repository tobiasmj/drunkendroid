package itu.malta.drunkendroidserver;

import java.util.LinkedList;

/**
 * 
 * Class Representing a Trip
 *
 */
public class Trip {

	long tripId = -1;
	String imeiNumber, name;
	long startTime; 
	long endTime = -1 ;

	LinkedList<Reading> readingCommands = new LinkedList<Reading>();
	
	Trip(int tripId) {
		this.tripId = tripId;
	}
	
	public Trip(int tripId, String imeiNumber, Long startTime, Long endTime, String name) {
		this.tripId = tripId;
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
	}
	
	public Trip(String imeiNumber, Long startTime, String name) {
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.name = name;
	}

	public Trip(String imeiNumber, Long startTime, Long endTime, String name) {
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
	}

	public long getTripId() {
		return tripId;
	}

	public void setTripId(long tripId) {
		this.tripId = tripId;
	}
	
	public String getImeiNumber() {
		return imeiNumber;
	}

	public String getName() {
		return name;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void addEvent(Reading readingComm) {
		readingCommands.add(readingComm);
	}
	public Reading getNextEvent() {
		return readingCommands.poll();
	}
	
	public boolean moreEvents() {
		return !readingCommands.isEmpty();
	}
}
