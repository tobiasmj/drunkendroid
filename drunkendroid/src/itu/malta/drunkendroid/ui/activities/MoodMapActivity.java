package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView; 
 
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

public class MoodMapActivity extends MapActivity {
	Location location;
	LocationManager locationManager;
	MapView mapView;
	MapController mapController;
	GeoPoint gp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);

    	String provider = locationManager.getBestProvider(criteria, true);
    	
    	if (provider != null) {
    		location = locationManager.getLastKnownLocation(provider);
    		/* TODO - adjust update times to minimize battery use. Can be changed as needed.
    		*
    		*/
    	} 
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        String coordinates[] = {"35.908138", "14.500975"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
        System.out.println("GPS: " + location.getLatitude() + "," + location.getLongitude());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        
        gp = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
 
        mapController.animateTo(gp);
        mapController.setZoom(17); 
        mapView.invalidate();
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
