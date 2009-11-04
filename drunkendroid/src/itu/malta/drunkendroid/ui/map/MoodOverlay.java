package itu.malta.drunkendroid.ui.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;

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
			
			// Create gradient circle
			int[] gradientColors = new int[]{Color.BLACK,Color.TRANSPARENT};
			float[] gradientPositions = new float[]{0.5f,1.0f};
			RadialGradient gradient = new RadialGradient(point.x, point.y, 40, gradientColors, gradientPositions, TileMode.CLAMP);
			
			// Create and setup paint brush
			Paint paint = new Paint();
			paint.setARGB(250, 255, 0, 0);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setShader(gradient); // Add gradient circle
			// Draw on the canvas
			canvas.drawPaint(paint);
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
