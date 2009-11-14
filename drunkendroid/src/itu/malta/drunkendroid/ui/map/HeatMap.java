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
	private Projection _projection;
	private int _zoomLevel;
	private int _radius;
	
	private HeatMap(MapView mapView)
	{
		_colorImage = createGradientImage(new Point(256, 1));
		_colorTable = createColorLookupTable(_colorImage,1.0f);
		_projection = mapView.getProjection();
		_bitmap = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Config.ARGB_8888);
	}
	
	public static HeatMap getInstance(MapView mapView)
	{
		if(_instance == null) _instance = new HeatMap(mapView);
		return _instance;
	}
	
	public void addGeoPoint(GeoPoint gp)
	{
		if(!_moods.contains(gp))
			_moods.add(gp);
	}
	
	public Bitmap createHeatmap(MapView mapView)
	{
		System.out.println("Creating heatmap..");
		
        Bitmap bitmap = Bitmap.createBitmap(_bitmap.getWidth(),_bitmap.getHeight(),Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		if(mapView.getZoomLevel() != _zoomLevel)
			calculateRadius(mapView.getZoomLevel());

		// Drawing geopoints
        for(GeoPoint gp : _moods)
        {
        	canvas = drawCircle(canvas, gp);
        }
        
        System.out.println("Start test loop");
        for (int i = 0; i < 320; i++)
        {
        	for (int j = 0; j < 480; j++)
        	{
        		
        	}
        }
        System.out.println("End test loop");

        System.out.println("Start bitmap loop");
		for (int i = 0; i < bitmap.getWidth(); ++i)
		{ 
			for (int j = 0; j < bitmap.getHeight(); ++j)
			{
			}
		}
        System.out.println("End bitmap loop");


		//int alpha = Color.alpha(bmp.getPixel(i, j));
		//if(alpha != 0)
		//	bitmap.setPixel(i, j, _colorTable[alpha]);
		
		System.out.println("Heatmap created ...");
        return bitmap;
	}
	
	private static Bitmap createGradientImage(Point size)
	{
		Bitmap bmp = Bitmap.createBitmap( size.x, size.y, Config.ARGB_8888);
		Canvas tempCanvas = new Canvas(bmp);
		Paint paint = new Paint();
		int[] gradientColors = new int[]{Color.WHITE, 
											Color.RED, 
											Color.YELLOW, 
											Color.GREEN, 
											Color.CYAN, 
											Color.BLUE, 
											Color.argb(0, 150, 150, 150)}; // Should be dark blue
		LinearGradient gradient = new LinearGradient(0, 0, bmp.getWidth(), bmp.getHeight(), gradientColors, null, TileMode.CLAMP);
		paint.setShader(gradient);
		
		tempCanvas.drawPaint(paint);

		return bmp;
	}
	
	private static int[] createColorLookupTable(Bitmap bmp, float alpha)
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
	
    private Canvas drawCircle(Canvas canvas, GeoPoint gp)
    {
		Point p = new Point();
		_projection.toPixels(gp, p);
		
		// Create gradient circle
		int[] gradientColors = new int[]{Color.BLACK,Color.TRANSPARENT};
		Shader gradientShader = new RadialGradient(p.x, p.y, _radius, gradientColors, null, TileMode.CLAMP);
		
		// Create and setup paint brush
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(gradientShader); // Add gradient circle
		canvas.drawPaint(paint);

		return canvas;
    }
}