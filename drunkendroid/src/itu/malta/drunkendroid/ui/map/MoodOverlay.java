package itu.malta.drunkendroid.ui.map;

import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MoodOverlay extends Overlay
{
    private HeatMap _heatmap = HeatMap.getInstance();;
    private MapView _mapView;
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			// Create a heatmap if map is null
			if(_mapView == null)
			{
				Double lat1 = 35.908138*1E6;
				Double lng1 = 14.500975*1E6;
				Double lat2 = 35.909319*1E6;
				Double lng2 = 14.503658*1E6;
				Double lat3 = 35.909027*1E6;
				Double lng3 = 14.504624*1E6;
				Double lat4 = 35.909197*1E6;
				Double lng4 = 14.504361*1E6;
				Double lat5 = 35.909162*1E6;
				Double lng5 = 14.503937*1E6;
				
		        GeoPoint gp1 = new GeoPoint(lat1.intValue(), lng1.intValue());
		        GeoPoint gp2 = new GeoPoint(lat2.intValue(), lng2.intValue());
		        GeoPoint gp3 = new GeoPoint(lat3.intValue(), lng3.intValue());
		        GeoPoint gp4 = new GeoPoint(lat4.intValue(), lng4.intValue());
		        GeoPoint gp5 = new GeoPoint(lat5.intValue(), lng5.intValue());

		        MoodMapPoint mp1 = new MoodMapPoint(gp1, 48);
		        MoodMapPoint mp2 = new MoodMapPoint(gp2, 255);
		        MoodMapPoint mp3 = new MoodMapPoint(gp3, 168);
		        MoodMapPoint mp4 = new MoodMapPoint(gp4, 98);
		        MoodMapPoint mp5 = new MoodMapPoint(gp5, 145);
		        
				_heatmap.addMoodMapPoint(mp1);
				_heatmap.addMoodMapPoint(mp2);
				_heatmap.addMoodMapPoint(mp3);
				_heatmap.addMoodMapPoint(mp4);
				_heatmap.addMoodMapPoint(mp5);

				_mapView = mapView;
				System.out.println("Creating heatmap");
				canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);
			}
			// Update heatmap if map has changed
			else //if(!_mapView.getMapCenter().equals(mapView.getMapCenter()))
			{
				System.out.println("Updating heatmap");
				_mapView = mapView;
				canvas.drawBitmap(_heatmap.createHeatmap(_mapView), 0, 0, null);	
			}
			// Reuse heatmap if map is unchanged
			/*
			else
			{
				System.out.println("Reusing heatmap - " + _mapView.getMapCenter() + " = " + mapView.getMapCenter());
				canvas.drawBitmap(_heatmap.getHeatmap(), 0, 0, null);
			}
			*/
		}
	}
}
