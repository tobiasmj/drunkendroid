package itu.malta.drunkendroid.ui;

import itu.malta.drunkendroid.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView; 
 
import android.os.Bundle;

public class MoodMapActivity extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	GeoPoint gp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        String coordinates[] = {"35.908138", "14.500975"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        gp = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mapController.animateTo(gp);
        mapController.setZoom(17); 
        mapView.invalidate();
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
