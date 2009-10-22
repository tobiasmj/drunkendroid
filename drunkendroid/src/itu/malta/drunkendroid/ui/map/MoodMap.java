package itu.malta.drunkendroid.ui.map;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.R.id;
import itu.malta.drunkendroid.R.layout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;  
 
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MoodMap extends MapActivity {
	
	MapView mapView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView) findViewById(R.id.mapView);
        LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
        View zoomView = mapView.getZoomControls(); 
 
        zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
					            LayoutParams.WRAP_CONTENT, 
					            LayoutParams.WRAP_CONTENT)); 
        mapView.displayZoomControls(true);
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}