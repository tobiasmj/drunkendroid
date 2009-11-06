package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	
	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_PREVIOUS_TRIPS = Menu.FIRST + 1;
	private static final int MENU_UPLOAD = Menu.FIRST + 2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				Intent i = new Intent("NEW_MOOD_READING");
				i.addCategory("itu.malta.drunkendroid");
				i.putExtra("mood", (short)seekBar.getProgress());
				
				sendBroadcast(i);
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {}

			public void onStartTrackingTouch(SeekBar seekBar) {}
		});
        
        final Button startServiceBtn = (Button)findViewById(R.id.startServiceBtn);
        
        startServiceBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			try {
    				Intent i = new Intent("TOGGLE_DRUNKEN_SERVICE");
    				i.addCategory("itu.malta.drunkendroid.services");
    				startService(i);
    			}
    			catch (Exception e) {
    				System.out.println(e.getMessage());
    			}
    		}
    	});
        
        final Button stopServiceBtn = (Button)findViewById(R.id.stopServiceBtn);
        stopServiceBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			try {
    				Intent i = new Intent("TOGGLE_DRUNKEN_SERVICE");
    				i.addCategory("itu.malta.drunkendroid.services");
    				stopService(i);
    			}
    			catch (Exception e) {
    				System.out.println(e.getMessage());
    			}
    		}
    	});
        
        final Button mvBtn = (Button)findViewById(R.id.mapViewButton);
        
    	mvBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			try {
    				Intent i = new Intent("VIEW_MAP");
    				startActivity(i);
    			}
    			catch (Exception e) {
    				System.out.println(e.getMessage());
    			}
    		}
    	});
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MainActivity.MENU_SETTINGS, 0, R.string.menu_settings).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MainActivity.MENU_PREVIOUS_TRIPS, 0, R.string.menu_previous_trips).setIcon(android.R.drawable.ic_menu_mapmode);
		menu.add(0, MainActivity.MENU_UPLOAD, 0, R.string.menu_upload).setIcon(android.R.drawable.ic_menu_upload);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;
		
		switch(item.getItemId()) {
		case MainActivity.MENU_SETTINGS:
			try {
			intent = new Intent("VIEW_SETTINGS");
			startActivity(intent);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			break;
		case MainActivity.MENU_PREVIOUS_TRIPS:
			try {
				intent = new Intent("VIEW_PREVIOUS_TRIPS");
				startActivity(intent);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		case MainActivity.MENU_UPLOAD:
			try {
				intent = new Intent("VIEW_UPLOAD");
				startActivity(intent);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			break;
	
		}		
		return super.onMenuItemSelected(featureId, item);
	}
}