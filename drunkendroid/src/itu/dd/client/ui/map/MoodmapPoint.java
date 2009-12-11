package itu.dd.client.ui.map;

import com.google.android.maps.GeoPoint;

public class MoodmapPoint {

	private GeoPoint _point;
	private int _mood;
	
	public MoodmapPoint(GeoPoint gp, int mood)
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
