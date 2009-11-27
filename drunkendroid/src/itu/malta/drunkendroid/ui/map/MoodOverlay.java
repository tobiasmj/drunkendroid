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
    private HeatMap _heatmap = HeatMap.getInstance();;
    private MapView _mapView;
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			// Create a heatmap if map is null
			if(!_mapView.getMapCenter().equals(mapView.getMapCenter()) || _mapView.getZoomLevel() != mapView.getZoomLevel())
			{
				if(_mapView == null)
					System.out.println("Creating heatmap");
				else
					System.out.println("Updating heatmap");
				_mapView = mapView;
				getMoodData(_mapView);
				canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);
			}
			// Reuse heatmap if map is unchanged
			else
			{
				System.out.println("Reusing heatmap");
				canvas.drawBitmap(_heatmap.getHeatmap(), 0, 0, null);
			}
		}
	}
	
	/**
	 * Calls the DataFacade that handles communication with server.
	 * If data is received then clear and add new points to heatmap.
	 * @param mapView
	 */
	private void getMoodData(MapView mapView)
	{
		_dataFacade = new DataFacade(mapView.getContext());
		
		List<ReadingEvent> data = _dataFacade.getReadingEvents(
				(long)130773960,
				(long)131027900,
				(double)(mapView.getMapCenter().getLatitudeE6()/1E6),
				(double)(mapView.getMapCenter().getLongitudeE6()/1E6),
				(long)1000);
		
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
	}
}
