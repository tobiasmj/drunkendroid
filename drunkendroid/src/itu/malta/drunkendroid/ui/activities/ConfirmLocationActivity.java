package itu.malta.drunkendroid.ui.activities;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import android.location.Location;
import android.os.Bundle;
import itu.malta.drunkendroid.*;

public class ConfirmLocationActivity extends MapActivity {
	MapView map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmactivity);

		map = (MapView)this.findViewById(R.id.confirmLocationMap);
		Location location = (Location)this.getIntent().getExtras().get("location");
		map.setBuiltInZoomControls(false);
        MapController mapController = map.getController();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        
        GeoPoint gp = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
        
        mapController.animateTo(gp);
        mapController.setZoom(16); 
        
        //SET OVERLAY WITH PIN ON EXACT LOCATION!!
        
        map.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
