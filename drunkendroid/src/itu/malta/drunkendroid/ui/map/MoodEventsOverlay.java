package itu.malta.drunkendroid.ui.map;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.MoodEvent;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MoodEventsOverlay extends Overlay {
	private ArrayList<MoodEvent> _events;
	private Projection _projection;
	
	public MoodEventsOverlay(ArrayList<MoodEvent> events)
	{
		_events = events;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			_projection = mapView.getProjection();
			
			for(Event e : _events)
			{
				Point p = _projection.toPixels(new GeoPoint((int)(e.latitude*1E6),(int)(e.longitude*1E6)), null);
				Paint paint = new Paint();
				paint.setColor(Color.YELLOW);
				canvas.drawCircle(p.x, p.y, 5, paint);
			}
		}
	}
}
