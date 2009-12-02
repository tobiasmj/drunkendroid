package itu.malta.drunkendroid.ui.activities;

import java.util.List;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.ui.map.MoodOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.os.Bundle;
import android.view.MotionEvent;

public class MoodMapActivity extends MapActivity {
	MapView _mapView;
	MapController _mapController;
	MoodOverlay _moodOverlay;
	GeoPoint _gp;
	
    /** Called when the activity is first created. */
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

        _mapController.setZoom(15);
        _mapController.animateTo(_gp, new Runnable() {
	        public void run()
	        {
	            // Add overlay
	            _moodOverlay = new MoodOverlay();
	            List<Overlay> overlays = _mapView.getOverlays();
	            overlays.add(_moodOverlay);
	        }
        });
    }
    
    public boolean onTouchEvent(MotionEvent event) {
    	
    	if(event.getAction() == MotionEvent.ACTION_DOWN)
    	{
    		System.out.println("DOWN");
    	}
    	else if(event.getAction() == MotionEvent.ACTION_UP)
    	{
    		System.out.println("UP");
    		
    	}
    	
    	return true;
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
