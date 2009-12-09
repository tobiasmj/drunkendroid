package itu.dd.server.interfaces;
/**
 * Interface for events that can be processed, all event types must implement this interface.
 * @author tobiasmj
 *
 */
public interface IEvent {
	public String getType();
	public double getLongitude();
	public void setLongitude(double longitude);
	public double getLatitude();
	public void setLatitude(double latitude);
	public void setTimeStamp(long timeStamp);
	public long getTimeStamp();
	public void setTripId(long tripId);
	public long getTripId();
}
