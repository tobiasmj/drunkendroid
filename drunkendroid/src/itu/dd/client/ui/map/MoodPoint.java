package itu.dd.client.ui.map;

import com.google.android.maps.GeoPoint;

public class MoodPoint {

	private GeoPoint _point;
	private int _mood;
	
	public MoodPoint(GeoPoint gp, int mood)
	{
		_point = gp;
		_mood = mood;
	}
	
	public GeoPoint getGeoPoint()
	{
		return _point;
	}
	
	public int getMood()
	{
		return _mood;
	}
}
