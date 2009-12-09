package itu.dd.client.ui.map;

import itu.dd.client.domain.CallEvent;
import itu.dd.client.domain.Event;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CallEventsOverlay extends Overlay {
	private ArrayList<CallEvent> _events;
	private Projection _projection;
	
	public CallEventsOverlay(ArrayList<CallEvent> events)
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
				paint.setColor(Color.BLUE);
				canvas.drawCircle(p.x, p.y, 5, paint);
			}
		}
	}
}
