package itu.dd.client.ui.activities;

import java.util.List;

import itu.dd.client.ui.map.MoodOverlay;
import itu.dd.client.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.os.Bundle;

public class MoodmapActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	MoodOverlay _moodOverlay;
	GeoPoint _gp;
	
    /** Called when the activity is created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
    	
        _mapView = (MapView)findViewById(R.id.mapView);
        _mapView.setBuiltInZoomControls(true);

        _mapController = _mapView.getController();
        
        double lat = 35.9232054039299*1E6;
        double lng = 14.489096835067395*1E6;
        
        _gp = new GeoPoint((int)lat,(int)lng);

        _mapController.setZoom(16);
        _mapController.animateTo(_gp, new Runnable() {
	        public void run()
	        {
	            List<Overlay> overlays = _mapView.getOverlays();
	            
	            // Add heatmap overlay
	            _moodOverlay = new MoodOverlay(MoodmapActivity.this, _mapView);
	            overlays.add(_moodOverlay);
	        }
        });
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
