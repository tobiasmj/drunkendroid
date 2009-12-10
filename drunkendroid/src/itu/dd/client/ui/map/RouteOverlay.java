package itu.dd.client.ui.map;

import itu.dd.client.domain.Event;
import itu.dd.client.domain.LocationEvent;
import itu.dd.client.R;

import java.util.ArrayList;
import java.util.Collections;

import android.content.res.Resources;
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

public class RouteOverlay extends Overlay {
	private ArrayList<LocationEvent> _events;
	private Projection _projection;
	private Resources _resources;
	
	public RouteOverlay(Resources res, ArrayList<LocationEvent> events)
	{
		_resources = res;
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
			Point p = null;
			
			Paint paint = new Paint();
			paint.setColor(Color.argb(150, 232, 21, 66));
			paint.setAntiAlias(true);
			paint.setStrokeWidth(5f);
			
			int length = _events.size();
			LocationEvent e;
			for(int i = 0; i < length; i++)
			{
				e = _events.get(i);
				System.out.print(e.getLatitude() + "x" + e.getLongitude());
				if(previous == null)
				{
					System.out.println(" er den første");
					p = _projection.toPixels(new GeoPoint((int)(e.getLatitude()*1E6),(int)(e.getLongitude()*1E6)), null);
					Bitmap bitmap = BitmapFactory.decodeResource(_resources,R.drawable.marker_trip_start);            
			        canvas.drawBitmap(bitmap, p.x-23, p.y-46, null); 
					canvas.drawCircle(p.x, p.y, 2, paint);
					previous = p;
				}
				else
				{
					System.out.println(" er blev forbundet");
					p = _projection.toPixels(new GeoPoint((int)(e.getLatitude()*1E6),(int)(e.getLongitude()*1E6)), null);
					canvas.drawLine(previous.x, previous.y, p.x, p.y, paint);
					canvas.drawCircle(p.x, p.y, 2, paint);
					previous = p;
				}
			}
			Bitmap bitmap = BitmapFactory.decodeResource(_resources,R.drawable.marker_trip_end);            
	        canvas.drawBitmap(bitmap, p.x-23, p.y-46, null); 
		}
	}
}
