package itu.malta.drunkendroid.ui.map;

import java.util.List;

import itu.malta.drunkendroid.control.DataFacade;
import itu.malta.drunkendroid.domain.ReadingEvent;
import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MoodOverlay extends Overlay
{
	private DataFacade _dataFacade;
    private HeatMap _heatmap = HeatMap.getInstance();
    private GeoPoint _mapCenter;
    private Integer _zoomLevel;
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			
			// Create heatmap for the first time
			if(_mapCenter == null && _zoomLevel == null)
			{
				System.out.println("Create heatmap");
				canvas = getHeatmap(canvas, mapView);
			}
			// Update heatmap if map is changed
			else if(!_mapCenter.equals(mapView.getMapCenter()) || _zoomLevel != mapView.getZoomLevel())
			{
				System.out.println("Update heatmap");
				canvas = getHeatmap(canvas, mapView);
			}
			// Reuse heatmap if map is unchanged
			else
			{
				System.out.println("Reuse heatmap");
				canvas = reuseHeatmap(canvas);
			}
		}
	}
	
	/**
	 * Creates a new heatmap, due to changes
	 * @param canvas
	 * @param mapView
	 * @return A heatmap canvas
	 */
	private Canvas getHeatmap(Canvas canvas, MapView mapView)
	{
		_mapCenter = mapView.getMapCenter();
		_zoomLevel = mapView.getZoomLevel();
		getMoodData(mapView);
		canvas.drawBitmap(_heatmap.createHeatmap(mapView), 0, 0, null);

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
	 * Calls the DataFacade that handles communication with server.
	 * If data is received then clear and add new points to heatmap.
	 * @param mapView
	 */
	private void getMoodData(MapView mapView)
	{
		System.out.println("Get moodmap");
		_dataFacade = new DataFacade(mapView.getContext());
		
		int latSpan = mapView.getLatitudeSpan();
		int longSpan = mapView.getLongitudeSpan();
		
		// Calculate an upper-left and a lower-right corner for the map.
		// The calculation adds 5 percent in each direction
		double ulLat = mapView.getMapCenter().getLatitudeE6()/1E6 + ((latSpan + latSpan * 0.05) / 2)/1E6;
		double ulLong = mapView.getMapCenter().getLongitudeE6()/1E6 - ((longSpan + longSpan * 0.05) / 2)/1E6;
		double lrLat = mapView.getMapCenter().getLatitudeE6()/1E6 - ((latSpan + latSpan * 0.05) / 2)/1E6;
		double lrLong = mapView.getMapCenter().getLongitudeE6()/1E6 + ((longSpan + longSpan * 0.05) / 2)/1E6;
		
		List<ReadingEvent> data = _dataFacade.getReadingEvents(
				(long)130773960,
				(long)131027900,
				(double)ulLat,
				(double)ulLong,
				(double)lrLat,
				(double)lrLong);
		
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
		System.out.println("Moodmap constructed");
	}
}
