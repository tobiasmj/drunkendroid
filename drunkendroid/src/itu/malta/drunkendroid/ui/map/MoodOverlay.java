package itu.malta.drunkendroid.ui.map;

import java.util.List;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.domain.MoodEventEvent;
import itu.malta.drunkendroid.tech.exception.RESTFacadeException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
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
    private GeoPoint _projectionCheck;
    private GeoPoint _mapCenter;
    private Integer _zoomLevel;
    private Point _startPoint;
    private boolean _firstDraw;
    private boolean _hasChanged;
    private boolean _hasZoomLevelChanged;
    private boolean _hasMapCenterChanged;
    private boolean _isTouched;
    private boolean _isHeatmapAvailable;
    private int _zoomCounts;
    
    public MoodOverlay(Context context, MapView mapView) {
    	_context = context;
    	_mapView = mapView;
    	_hasChanged = false;
    	_hasZoomLevelChanged = false;
    	_hasMapCenterChanged = false;
    	_isTouched = false;
    	_startPoint = new Point(0,0);
    	_projectionCheck = new GeoPoint(0,0);
    	_mapCenter = new GeoPoint(0,0);
    	_zoomLevel = 0;
    	_zoomCounts = 15;
    	_firstDraw = true;
    	_isHeatmapAvailable = true;
    }
    
    public boolean onTouchEvent(MotionEvent e, MapView mapView)
    {
    	if(e.getAction() == MotionEvent.ACTION_DOWN)
    		_isTouched = true;
    	else if(e.getAction() == MotionEvent.ACTION_UP)
    		_isTouched = false;
    	
    	return false;
    }
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			updateBooleans(mapView);
			
			if(_isHeatmapAvailable)
			{
				mapView.getController().stopAnimation(true);
				mapView.getController().stopPanning();
				
				// Create the first heatmap
				if(_firstDraw)
				{
					canvas = getHeatmap(canvas);
					_firstDraw = false;
					_hasChanged = false;
					_hasMapCenterChanged = false;
					_hasZoomLevelChanged = false;
				}
				// Create a new heatmap if it is changed
				else if(_hasChanged)
				{
					// Zoom level has changed, update map
					if(_hasZoomLevelChanged)
					{
						// Zoom calls draw lots of times
						// hack to only update the last one
						if(_zoomCounts > 0)
						{
							_zoomCounts--;
						}
						else
						{
							// Update map
							canvas = getHeatmap(canvas);
							_hasZoomLevelChanged = false;
							_hasChanged = false;
							_zoomCounts = 15;
						}
					}
					// Map is moved, move the overlay along
					else if(_hasMapCenterChanged)
					{
						if(!_isTouched)
						{
							canvas = getHeatmap(canvas);
							_hasMapCenterChanged = false;
							_hasChanged = false;
						}
						else
						{
							calculateMapMovements(mapView);
							canvas = moveHeatmap(canvas, _startPoint.x, _startPoint.y);
						}
					}
					// Reuse heatmap if map is unchanged
					else
					{
						canvas = reuseHeatmap(canvas);
					}
				}
				// Reuse heatmap if map is unchanged
				else
				{
					canvas = reuseHeatmap(canvas);
				}
			}
			// Don't draw heatmap - tell the user that it is
			// not available on this zoom level
			else
			{
				/*
				Paint background = new Paint();
				background.setColor(Color.BLACK);
				background.setAlpha(150);
				
				RectF rect = new RectF();
				rect.
				
				canvas.drawRoundRect(rect, 0, 0, background);
				*/
				
				Paint paint = new Paint();
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
				paint.setColor(Color.BLACK);
				paint.setAlpha(150);
				RectF rect = new RectF();
				rect.left = 16;
				rect.right = mapView.getWidth() - 16;
				rect.top = 10;
				rect.bottom = 40;
				canvas.drawRoundRect(rect, 4, 4, paint);
				
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Align.CENTER);
				paint.setAntiAlias(true);
				canvas.drawText("Can't draw mood on this zoomlevel. Please zoom in.", mapView.getWidth()/2, 29, paint);
			}
			
			// Update mapCenter and zoomLevel
			_mapCenter = mapView.getMapCenter();
			_zoomLevel = mapView.getZoomLevel();
			_projectionCheck = mapView.getProjection().fromPixels(0, 0);
		}
	}
	
	/**
	 * Calculates the movement from old map to new map
	 * @param mapView
	 */
	private void calculateMapMovements(MapView mapView)
	{
		Projection oldProj = _mapView.getProjection();
		Projection newProj = mapView.getProjection();

		Point oldStart = oldProj.toPixels(_mapCenter, null);
		Point newStart = newProj.toPixels(mapView.getMapCenter(), null);
		
		int startX = oldStart.x - newStart.x;
		int startY = oldStart.y - newStart.y;

		_startPoint = new Point(_startPoint.x+startX,_startPoint.y+startY);
	}
	
	/**
	 * Updates booleans used for map updates
	 * @param mapView
	 */
	private void updateBooleans(MapView mapView)
	{
		GeoPoint currentProjection = mapView.getProjection().fromPixels(0, 0);
		
		// if projection changes, set _isChanged to true
		if(_projectionCheck.getLatitudeE6() != currentProjection.getLatitudeE6() ||
				_projectionCheck.getLongitudeE6() != currentProjection.getLongitudeE6())
			_hasChanged = true;
		
		// if zoom level changes, set _isZoomlevelChanged to true
		if(_zoomLevel != mapView.getZoomLevel())
			_hasZoomLevelChanged = true;
		
		// if map center changes, set _isMapCenterChanged to true
		if(_mapCenter.getLatitudeE6() != mapView.getMapCenter().getLatitudeE6() ||
				_mapCenter.getLongitudeE6() != mapView.getMapCenter().getLongitudeE6())
			_hasMapCenterChanged = true;

		if(mapView.getZoomLevel() < 15)
			_isHeatmapAvailable = false;
		else
			_isHeatmapAvailable = true;
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
		canvas.drawBitmap(_heatmap.getHeatmap(), _startPoint.x, _startPoint.y, null);
		
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
	 * Calls the DataFacade that handles communication with server.
	 * If data is received then clear and add new points to heatmap.
	 * @param mapView
	 */
	private void getMoodData()
	{
		_dataFacade = new DataFacade(_mapView.getContext());
		
		int latSpan = _mapView.getLatitudeSpan();
		int longSpan = _mapView.getLongitudeSpan();
		
		// Calculate an upper-left and a lower-right corner for the map.
		// The calculation adds 5 percent in each direction
		double ulLat = _mapView.getMapCenter().getLatitudeE6()/1E6 + ((latSpan * 1.25) / 2)/1E6;
		double ulLong = _mapView.getMapCenter().getLongitudeE6()/1E6 - ((longSpan * 1.25) / 2)/1E6;
		double lrLat = _mapView.getMapCenter().getLatitudeE6()/1E6 - ((latSpan * 1.25) / 2)/1E6;
		double lrLong = _mapView.getMapCenter().getLongitudeE6()/1E6 + ((longSpan * 1.25) / 2)/1E6;
		
		List<MoodEventEvent> data = null;

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
			
			for(MoodEventEvent event : data)
			{
				GeoPoint gp = new GeoPoint((int)(event.latitude*1E6), (int)(event.longitude*1E6));
				MoodMapPoint mp = new MoodMapPoint(gp, event.mood);
				_heatmap.addMoodMapPoint(mp);
			}
		}
	}
}
