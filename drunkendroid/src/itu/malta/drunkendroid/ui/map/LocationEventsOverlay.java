package itu.malta.drunkendroid.ui.map;

import itu.malta.drunkendroid.domain.Event;
import itu.malta.drunkendroid.domain.LocationEvent;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LocationEventsOverlay extends Overlay {
	private ArrayList<LocationEvent> _events;
	private Projection _projection;
	
	public LocationEventsOverlay(ArrayList<LocationEvent> events)
	{
		_events = events;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			_projection = mapView.getProjection();
			
			// Sort events by time
			Collections.sort(_events);
			
			Point previous = null;
			for(Event e : _events)
			{
				Paint paint = new Paint();
				paint.setColor(Color.GREEN);

				if(previous == null)
				{
					Point p = _projection.toPixels(new GeoPoint((int)(e.getLatitude()*1E6),(int)(e.getLongitude()*1E6)), null);
					canvas.drawCircle(p.x, p.y, 5, paint);
					previous = p;
				}
				else
				{
					Point p = _projection.toPixels(new GeoPoint((int)(e.getLatitude()*1E6),(int)(e.getLongitude()*1E6)), null);
					canvas.drawLine(previous.x, previous.y, p.x, p.y, paint);
					canvas.drawCircle(p.x, p.y, 5, paint);
					previous = p;
				}
			}
		}
	}
}
