package itu.malta.drunkendroid.ui.activities;

import java.util.List;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.ui.map.MoodOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
 
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MoodMapActivity extends MapActivity {
	Location location;
	LocationManager locationManager;
	MapView mapView;
	MapController mapController;
	MoodOverlay moodOverlay;
	GeoPoint gp;
	int t = 5000;
	int distance = 10;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        
    	locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	
    	String provider = locationManager.getBestProvider(getCriteria(), true);
    	System.out.println("provider is " + provider.toString());
    	if (provider != null) {
    		location = locationManager.getLastKnownLocation(provider);
    		/* TODO - adjust update times to minimize battery use. Can be changed as needed.
    		*
    		*/
    	}

    	LocationListener locationListener = new LocationListener()
    	{
    		public void onLocationChanged(Location location)
    		{
    			System.out.println("Location has changed!!!");
    			// Update application based on new location.
    			updateWithNewLocation(location);
    		}
    		public void onProviderDisabled(String provider)
    		{
    			// Update application if provider is disabled.
    			updateWithNewLocation(null);
    		}
    		public void onProviderEnabled(String provider)
    		{
    			// Update application if provider is enabled.
    		}
    		public void onStatusChanged(String provider, int status, Bundle extras)
    		{
    			// Update application if provider hardware status changed.
    		}
    	};
    	
    	//updateWithNewLocation(location);
    	
    	locationManager.requestLocationUpdates("gps", t, distance, locationListener);
    	
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        mapController = mapView.getController();
        String coordinates[] = {"35.908138", "14.500975"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
        
        gp = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
        
        mapController.animateTo(gp);
        mapController.setZoom(17); 

        // Add overlay
        moodOverlay = new MoodOverlay();
        List<Overlay> overlays = mapView.getOverlays();
        overlays.add(moodOverlay);
        
        mapView.invalidate();
    }
    
    private void updateWithNewLocation(Location location)
    {
    	Double lat,lng;
    	if(location != null)
    	{
    		lat = location.getLatitude()*1E6;
    		lng = location.getLongitude()*1E6;
    		GeoPoint pt = new GeoPoint(lat.intValue(),lng.intValue());
    		if(lat != 0.0 && lng != 0.0)
    			mapController.animateTo(pt);
    		System.out.println(lat + "x" + lng);
    	}
    	else
    		System.out.println("No location.");
    }
    
    private Criteria getCriteria()
    {
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
    	
    	return criteria;
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
