package itu.malta.drunkendroid.ui.map;

import com.google.android.maps.GeoPoint;

public class MoodMapPoint {

	private GeoPoint _point;
	private int _mood;
	
	public MoodMapPoint(GeoPoint gp, int mood)
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
