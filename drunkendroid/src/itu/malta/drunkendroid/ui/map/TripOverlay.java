package itu.malta.drunkendroid.ui.map;

import itu.malta.drunkendroid.domain.Event;

import java.util.ArrayList;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class TripOverlay extends Overlay {
	private ArrayList<Event> _events;
	private Projection _projection;
	
	public TripOverlay(ArrayList<Event> events)
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
				paint.setColor(Color.RED);
				canvas.drawCircle(p.x, p.y, 5, paint);
			}
		}
	}
}
