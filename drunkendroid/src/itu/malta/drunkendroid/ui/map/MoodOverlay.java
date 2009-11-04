package itu.malta.drunkendroid.ui.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MoodOverlay extends Overlay
{
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		Projection projection = mapView.getProjection();
		Double lat = 35.908138*1E6;
		Double lng = 14.500975*1E6;
		
		GeoPoint gp = new GeoPoint(lat.intValue(), lng.intValue());
		
		if(shadow == false)
		{
			Point point = new Point();
			projection.toPixels(gp, point);
			
			// Create and setup paint brush
			Paint paint = new Paint();
			paint.setARGB(250, 255, 0, 0);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			
			// Create circle
			int rad = 5;
			RectF oval = new RectF(point.x-rad, point.y-rad,point.x+rad,point.y+rad);
			
			// Draw on the canvas
			canvas.drawOval(oval, paint);
			canvas.drawText("Red circle", point.x+rad, point.y+rad, paint);
		}
		else
		{
			
		}
	}
	
	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView)
	{
		// Return true if screen tap is handled by this overlay
		return false;
	}
}
