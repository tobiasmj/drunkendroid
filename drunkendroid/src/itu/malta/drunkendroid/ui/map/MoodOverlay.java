package itu.malta.drunkendroid.ui.map;

import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MoodOverlay extends Overlay
{
    private HeatMap _heatmap;
    
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if(shadow == false)
		{
			_heatmap = HeatMap.getInstance(mapView);
			
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
	        
			_heatmap.addGeoPoint(gp1);
			_heatmap.addGeoPoint(gp2);
			_heatmap.addGeoPoint(gp3);
			_heatmap.addGeoPoint(gp4);
			_heatmap.addGeoPoint(gp5);
			
			if(mapView.getZoomLevel() > 12)
				_heatmap.createHeatmap(mapView);
		}
	}
    
	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView)
	{
		// Return true if screen tap is handled by this overlay
		return false;
	}
}
