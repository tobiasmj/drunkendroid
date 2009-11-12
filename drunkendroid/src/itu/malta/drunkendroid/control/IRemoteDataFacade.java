package itu.malta.drunkendroid.control;

import itu.malta.drunkendroid.domain.*;

public interface IRemoteDataFacade {
	public Trip getEvents(Long starTime, Long endTime, Long latitude, Long longitude, Long distance);
	public Long uploadTrip(Trip t);
	public Long updateTrip(Trip t, Event e);
}
