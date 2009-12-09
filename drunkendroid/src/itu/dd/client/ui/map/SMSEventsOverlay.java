package itu.dd.client.ui.map;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.SMSEvent;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class SMSEventsOverlay extends Overlay {
	private ArrayList<SMSEvent> _events;
	private Projection _projection;
	
	public SMSEventsOverlay(ArrayList<SMSEvent> events)
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
				Point p = _projection.toPixels(new GeoPoint((int)(e.getLatitude()*1E6),(int)(e.getLongitude()*1E6)), null);
				Paint paint = new Paint();
				paint.setColor(Color.RED);
				canvas.drawCircle(p.x, p.y, 5, paint);
			}
		}
	}
}