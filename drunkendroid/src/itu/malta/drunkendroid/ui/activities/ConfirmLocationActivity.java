package itu.malta.drunkendroid.ui.activities;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import itu.malta.drunkendroid.*;

public class ConfirmLocationActivity extends MapActivity {
	MapView map;
	Location location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmactivity);

		map = (MapView)this.findViewById(R.id.confirmLocationMap);
		location = (Location)this.getIntent().getExtras().get("location");
		map.setBuiltInZoomControls(false);
        MapController mapController = map.getController();
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        
        GeoPoint gp = new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6));
        
        mapController.animateTo(gp);
        mapController.setZoom(16); 
        
        //SET OVERLAY WITH PIN ON EXACT LOCATION!!
        
        map.invalidate();
        
        Button yesBtn = (Button)this.findViewById(R.id.confirmLocationYesBtn);
        yesBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
        });
        
        Button noBtn = (Button)this.findViewById(R.id.confirmLocationNoBtn);
        noBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(ConfirmLocationActivity.this, itu.malta.drunkendroid.control.services.DrunkenService.class);
				i.putExtra("deleteLocation", true);
				i.putExtra("location", location);
				startService(i);
				finish();
			}
        });
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
