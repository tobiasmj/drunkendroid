package itu.malta.drunkendroid.ui.map;

import java.util.List;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MoodOverlay extends Overlay
{
	private DataFacade _dataFacade;
    private HeatMap _heatmap = HeatMap.getInstance();
    private MapView _mapView;
    private Context _context;
    
    // Used to check updates on map
    private GeoPoint _mapCenter;
    private Integer _zoomLevel;
    private Point _startPoint;
    private boolean _changed;
    private GeoPoint _projectionCheck;
    
    public MoodOverlay(Context context, MapView mapView) {
    	_context = context;
    	_mapView = mapView;
    	_changed = false;
    	_startPoint = new Point(0,0);
    	_projectionCheck = new GeoPoint(0,0);
    }
    
    public void clicked()
    {
    	Log.i("DrunkDroid", "YEEEEEEEEP!!!!!!!!!!!!!");
    }
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			mapView.getController().stopAnimation(true);
			mapView.getController().stopPanning();
			
			GeoPoint currentProjection = mapView.getProjection().fromPixels(0, 0);
			
			if(_projectionCheck.getLatitudeE6() != currentProjection.getLatitudeE6() ||
					_projectionCheck.getLongitudeE6() != currentProjection.getLongitudeE6())
			{
				Log.i("DrunkDroid","Projection has changed!!!");
				_changed = true;
			}
			
			// Create heatmap for the first time
			if(_heatmap.getHeatmap() == null)
			{
				Log.i("DrunkDroid", "Create heatmap");
				canvas = getHeatmap(canvas);
			}
			// If map has just changed, request new map
			else if(_changed)
			{
				Log.i("DrunkDroid","Updating heatmap");
				canvas = getHeatmap(canvas);
				_changed = false;
			}
			// Map is moved, move the overlay along
			else if(_mapCenter.getLatitudeE6() != mapView.getMapCenter().getLatitudeE6() ||
					_mapCenter.getLongitudeE6() != mapView.getMapCenter().getLongitudeE6())
			{
				Log.i("DrunkDroid", "Moving heatmap");
				Projection oldProj = _mapView.getProjection();
				Projection newProj = mapView.getProjection();

				Point oldStart = oldProj.toPixels(_mapCenter, null);
				Point newStart = newProj.toPixels(mapView.getMapCenter(), null);
				
				int startX = oldStart.x - newStart.x;
				int startY = oldStart.y - newStart.y;

				_startPoint = new Point(_startPoint.x+startX,_startPoint.y+startY);
				
				canvas = moveHeatmap(canvas, _startPoint.x, _startPoint.y);
				_changed = true;
			}
			// Map is zoomed, zoom the overlay too
			else if(_zoomLevel != mapView.getZoomLevel())
			{
				Log.i("DrunkDroid", "Zooming heatmap");
				if(_zoomLevel > mapView.getZoomLevel())
					canvas = zoomHeatmap(canvas, -1);
				else
					canvas = zoomHeatmap(canvas, 1); 
				_changed = true;
			}
			// Reuse heatmap if map is unchanged
			else
			{
				Log.i("DrunkDroid", "Reuse heatmap");
				canvas = reuseHeatmap(canvas);
			}
			
			// Update mapCenter and zoomlevel
			_mapCenter = mapView.getMapCenter();
			_zoomLevel = mapView.getZoomLevel();
			_projectionCheck = mapView.getProjection().fromPixels(0, 0);
		}
	}
	
	/**
	 * Creates a new heatmap, due to changes
	 * @param canvas
	 * @param mapView
	 * @return A heatmap canvas
	 */
	private Canvas getHeatmap(Canvas canvas)
	{
		getMoodData();
		canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);
		_startPoint = new Point(0,0); // Reset bitmap upper-left point

		return canvas;
	}
	
	/**
	 * Reuses the last created heatmap
	 * @param canvas
	 * @return A heatmap canvas
	 */
	private Canvas reuseHeatmap(Canvas canvas)
	{
		canvas.drawBitmap(_heatmap.getHeatmap(), 0, 0, null);
		
		return canvas;
	}
	
	/**
	 * Move the last created heatmap
	 * @param canvas
	 * @return A heatmap canvas
	 */
	private Canvas moveHeatmap(Canvas canvas, int x, int y)
	{
		canvas.drawBitmap(_heatmap.getHeatmap(), x, y, null);
		
		return canvas;
	}
	
	/**
	 * Zoom the last created heatmap
	 * @param canvas
	 * @return A heatmap canvas
	 */
	private Canvas zoomHeatmap(Canvas canvas, int zoomWay)
	{
		Bitmap bitmap = _heatmap.getHeatmap();
		if(zoomWay == -1)
		{
			canvas.drawBitmap(bitmap, 0, 0, null);
			canvas.scale(0.5f, 0.5f, (float)(bitmap.getWidth())/4, (float)(bitmap.getHeight())*3/4);
		}
		else if(zoomWay == 1)
		{
			canvas.drawBitmap(bitmap, -bitmap.getWidth()/4, -bitmap.getHeight()/4, null);
			canvas.scale(2f, 2f, -(float)(bitmap.getWidth())*1/2, (float)(bitmap.getWidth())*2);
		}
		
		return canvas;
	}
	
	/**
	 * Calls the DataFacade that handles communication with server.
	 * If data is received then clear and add new points to heatmap.
	 * @param mapView
	 */
	private void getMoodData()
	{
		Log.i("DrunkDroid", "Get moodmap");
		_dataFacade = new DataFacade(_mapView.getContext());
		
		int latSpan = _mapView.getLatitudeSpan();
		int longSpan = _mapView.getLongitudeSpan();
		
		// Calculate an upper-left and a lower-right corner for the map.
		// The calculation adds 5 percent in each direction
		double ulLat = _mapView.getMapCenter().getLatitudeE6()/1E6 + ((latSpan * 1.2) / 2)/1E6;
		double ulLong = _mapView.getMapCenter().getLongitudeE6()/1E6 - ((longSpan * 1.2) / 2)/1E6;
		double lrLat = _mapView.getMapCenter().getLatitudeE6()/1E6 - ((latSpan * 1.2) / 2)/1E6;
		double lrLong = _mapView.getMapCenter().getLongitudeE6()/1E6 + ((longSpan * 1.2) / 2)/1E6;

		Log.i("DrunkDroid","MapView UL: " + ulLat + "x" + ulLong);
		Log.i("DrunkDroid","MapView LR: " + lrLat + "x" + lrLong);
		
		List<ReadingEvent> data = null;

		try {
			data = _dataFacade.getReadingEvents(
					(long)130773960,
					(long)131027900,
					(double)ulLat,
					(double)ulLong,
					(double)lrLat,
					(double)lrLong);
		} catch (RESTFacadeException e) {
			new AlertDialog.Builder(_context)
		      .setMessage("Could not connect to server. Please check your connection and try again.")
		      .setPositiveButton("Ok", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {	
					((Activity)_context).finish();
				}}).show();
		}
		
		if(data != null)
		{
			_heatmap.clearMoodMapPoints();
			
			for(ReadingEvent event : data)
			{
				GeoPoint gp = new GeoPoint((int)(event.latitude*1E6), (int)(event.longitude*1E6));
				MoodMapPoint mp = new MoodMapPoint(gp, event.mood);
				_heatmap.addMoodMapPoint(mp);
			}
		}
		Log.i("DrunkDroid", "Moodmap constructed");
	}
}
