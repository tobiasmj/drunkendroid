package itu.malta.drunkendroid.ui.map;

import java.util.ArrayList;

import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.util.Log;

public class HeatMap{

	private static HeatMap _instance = null;
	private static int[] _colorTable;
	private ArrayList<MoodMapPoint> _moods = new ArrayList<MoodMapPoint>();
	private Bitmap _bitmap;
	private int _zoomLevel;
	
	private HeatMap()
	{
		_colorTable = ColorTable.getColorTable();
	}
	
	public static HeatMap getInstance()
	{
		if(_instance == null) _instance = new HeatMap();
		return _instance;
	}
	
	/**
	 * Adds a MoodMapPoint to ArrayList _moods
	 * @param mp
	 */
	public void addMoodMapPoint(MoodMapPoint mp)
	{
		if(!_moods.contains(mp))
			_moods.add(mp);
	}
	
	/**
	 * Clears the ArrayList of MoodMapPoints
	 */
	public void clearMoodMapPoints()
	{
		_moods.clear();
	}
	
	/**
	 * 
	 * @return a bitmap containing the heatmap
	 */
	public Bitmap getHeatmap()
	{
		return _bitmap;
	}
	
	/**
	 * Creates a heatmap from the MoodMapPoints in _moods
	 * @param mapView
	 * @return a bitmap containing the heatmap
	 */
	public Bitmap createHeatmap(MapView mapView)
	{
		Log.i("DrunkDroid", "Create heatmap");
		Projection projection = mapView.getProjection();
		Bitmap bitmap = Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		if(mapView.getZoomLevel() != _zoomLevel)
			_zoomLevel = mapView.getZoomLevel();

		Log.i("DrunkDroid", "Draw points (" + _moods.size() + ")");
		// Drawing MoodMapPoints
        for(MoodMapPoint mp : _moods)
        	canvas = drawCircle(canvas, projection, mp);
		Log.i("DrunkDroid", "Points drawn");

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        int[] pixels = new int[width*height];
        
        Log.i("DrunkDroid", "Color bitmap");
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        
        // Change alpha value to the appropriate color from the color table
        for(int i = 0; i < pixels.length; i++)
        {
        	int alpha = Color.alpha(pixels[i]);
        	if(alpha > 0)
        		pixels[i] = _colorTable[alpha];
        }
        
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Log.i("DrunkDroid", "Bitmap colored");
        
        _bitmap = bitmap;

		Log.i("DrunkDroid", "Heatmap created");
        return _bitmap;
	}
	
	/**
	 * Calculate a radius with regards to the zoomLevel
	 * @param zoomLevel
	 */
	private int getRadius()
	{
		switch(_zoomLevel)
		{
			case 19: return 200;
			case 18: return 100;
			case 17: return 50;
			default: return 25;
		}
	}
	
	/**
	 * Draw a semi-transparent white circle on the canvas
	 * @param canvas
	 * @param projection
	 * @param mp
	 * @return a canvas with the circle drawn upon it
	 */
    private Canvas drawCircle(Canvas canvas, Projection projection, MoodMapPoint mp)
    {
		Point p = projection.toPixels(mp.getGeoPoint(), null);
		int radius = 1;
		if(getRadius() > 1)
			radius = getRadius(); 
		
		Log.i("DrunkDroid", p.x + "x" + p.y + "(" + radius + ")");
		// Create gradient circle
		int[] gradientColors = new int[]{Color.argb(mp.getMood(), 255, 255, 255), Color.TRANSPARENT};
		Shader gradientShader = new RadialGradient(p.x, p.y, radius, gradientColors, null, TileMode.CLAMP);
		
		// Create and setup paint brush
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(gradientShader); // Add gradient circle
		canvas.drawPaint(paint);

		return canvas;
    }
}