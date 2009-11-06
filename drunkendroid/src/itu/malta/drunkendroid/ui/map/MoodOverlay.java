package itu.malta.drunkendroid.ui.map;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
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
		int radius = mapView.getZoomLevel();
		
		GeoPoint gp = new GeoPoint(lat.intValue(), lng.intValue());
		
		if(shadow == false)
		{
			Point point = new Point();
			projection.toPixels(gp, point);
			Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			
			// Create gradient circle
			int[] gradientColors = new int[]{Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE,Color.TRANSPARENT};
			float[] gradientPositions = new float[]{0.4f,0.6f,0.8f,0.8f,0.2f};
			Shader gradientShader = new RadialGradient(point.x, point.y, 40, gradientColors, null, TileMode.CLAMP);
			Shader gradientShaderTwo = new RadialGradient(point.x-20, point.y-20, 40, gradientColors, null, TileMode.CLAMP);
			Shader bitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
			Shader composerShader = new ComposeShader(gradientShader, bitmapShader, new PorterDuffXfermode(Mode.MULTIPLY));
			Shader composerShaderTwo = new ComposeShader(gradientShaderTwo, bitmapShader, new PorterDuffXfermode(Mode.MULTIPLY));
			
			// Create and setup paint brush
			Paint paint = new Paint();
			paint.setARGB(250, 255, 0, 0);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setShader(gradientShader); // Add gradient circle
			// Draw on the canvas
			canvas.drawPaint(paint);
			
			Paint paintTwo = new Paint();
			paintTwo.setShader(gradientShaderTwo);
			canvas.drawPaint(paintTwo);
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
