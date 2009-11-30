package itu.malta.drunkendroidserver.domain;

import itu.malta.drunkendroidserver.interfaces.IEvent;

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

	LinkedList<IEvent> events = new LinkedList<IEvent>();
	/**
	 * Constructor, used when only the tripId is known eg. when getting an event from the database.
	 * @param tripId
	 */
	public Trip(int tripId) {
		this.tripId = tripId;
	}
	
	/**
	 * Constructor, used when returning a trip from the database.
	 * @param tripId
	 * @param imeiNumber, the phone imeiNumber.
	 * @param startTime, the start time of the trip.
	 * @param endTime, the end time of the Trip.
	 * @param name, the name of the trip.
	 */
	public Trip(int tripId, String imeiNumber, Long startTime, Long endTime, String name) {
		this.tripId = tripId;
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
	}
	/**
	 * Constructor, for use when the trip is not closed.
	 * @param imeiNumber, the phone imeiNumber.
	 * @param startTime, the start time of the trip.
	 * @param name, the name of the trip.
	 */
	public Trip(String imeiNumber, Long startTime, String name) {
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.name = name;
	}
	/**
	 * Constructor, for use when adding a new closed event to the database
	 * @param imeiNumber
	 * @param startTime
	 * @param endTime
	 * @param name
	 */
	public Trip(String imeiNumber, Long startTime, Long endTime, String name) {
		this.imeiNumber = imeiNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
	}
	
	/**
	 * Constructor 
	 * @param startTime
	 * @param endTime
	 * @param name
	 */
	public Trip(Long startTime,Long endTime, String name) {
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
	/**
	 * method for adding a single event to the trip.
	 * @param event and IEvent
	 */
	public void addEvent(IEvent event) {
		this.events.add(event);
	}
	/**
	 * method for adding multiple events to the database, old events will be lost.
	 * @param events, linked list of IEvents
	 */
	public void addEvents(LinkedList<IEvent> events) {
		this.events = events;
	}
	
	/**
	 * method for getting the next event in the list, the event is removed from the internal list.
	 * @return an IEvent.
	 */
	public IEvent getNextEvent() {
		return events.poll();
	}
	/**
	 * method for testing if there are more events in the linked list.
	 * @return
	 */
	public boolean moreEvents() {
		return !events.isEmpty();
	}
	public void setImeiNumber(String imeiNumber) {
		this.imeiNumber = imeiNumber;
	}
}
