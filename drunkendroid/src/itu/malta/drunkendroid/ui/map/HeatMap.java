package itu.malta.drunkendroid.ui.map;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;

public class HeatMap{

	private static HeatMap _instance = null;
	private ArrayList<GeoPoint> _moods = new ArrayList<GeoPoint>();
	private Bitmap _colorImage;
	private int[] _colorTable;
	private Bitmap _bitmap;
	private int _zoomLevel;
	private int _radius;
	
	private HeatMap()
	{
		_colorImage = createGradientImage();
		_colorTable = createColorLookupTable(_colorImage);
	}
	
	public static HeatMap getInstance()
	{
		if(_instance == null) _instance = new HeatMap();
		return _instance;
	}
	
	public void addGeoPoint(GeoPoint gp)
	{
		if(!_moods.contains(gp))
			_moods.add(gp);
	}
	
	public Bitmap getHeatmap()
	{
		return _bitmap;
	}
	
	public Bitmap createHeatmap(MapView mapView)
	{
		Projection projection = mapView.getProjection();
		Bitmap bitmap = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		if(mapView.getZoomLevel() != _zoomLevel)
			calculateRadius(mapView.getZoomLevel());

		// Drawing geopoints
        for(GeoPoint gp : _moods)
        	canvas = drawCircle(canvas, projection, gp);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int[] pixels = new int[width*height];
        
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        
        // Change alpha value to the appropriate color from the color table
        for(int i = 0; i < pixels.length; i++)
        {
        	int alpha = Color.alpha(pixels[i]);
        	if(alpha > 0)
        		pixels[i] = _colorTable[alpha];
        }
        
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        
        _bitmap = bitmap;
        
        return _bitmap;
	}
	
	private static Bitmap createGradientImage()
	{
		Bitmap bmp = Bitmap.createBitmap(256, 1, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		int[] gradientColors = new int[]{Color.TRANSPARENT,
				 Color.argb(155, 255, 255, 255), // White
				 Color.argb(155, 74, 135, 248), // Blue
				 Color.argb(155, 128, 223, 59), // Green
				 Color.argb(155, 255, 200, 0), // Yellow
				 Color.argb(155, 216, 15, 15)}; // Red
		LinearGradient gradient = new LinearGradient(0, 0, bmp.getWidth(), bmp.getHeight(), gradientColors, null, TileMode.CLAMP);
		paint.setShader(gradient);
		
		canvas.drawPaint(paint);

		return bmp;
	}
	
	private static int[] createColorLookupTable(Bitmap bmp)
	{
		int tableSize = 256;
		int[] colorTable = new int[tableSize];
		for (int i = 0; i < tableSize; ++i)
			colorTable[i] = bmp.getPixel(i, 0);

		return colorTable;
	}
	
	private void calculateRadius(int zoomLevel)
	{
		_zoomLevel = zoomLevel;
		
		switch(zoomLevel)
		{
			case 19: _radius = 200; break;
			case 18: _radius = 100; break;
			case 17: _radius = 50; break;
			case 16: _radius = 25; break;
			case 15: _radius = 12; break;
			case 14: _radius = 6; break;
			case 13: _radius = 3; break;
			default: _radius = 1; break;
		}
	}
	
    private Canvas drawCircle(Canvas canvas, Projection projection, GeoPoint gp)
    {
		Point p = projection.toPixels(gp, null);
		
		// Create gradient circle
		int[] gradientColors = new int[]{Color.WHITE, Color.TRANSPARENT};
		Shader gradientShader = new RadialGradient(p.x, p.y, _radius, gradientColors, null, TileMode.CLAMP);
		
		// Create and setup paint brush
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(gradientShader); // Add gradient circle
		canvas.drawPaint(paint);

		return canvas;
    }
}