package itu.malta.drunkendroid.ui;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.R.id;
import itu.malta.drunkendroid.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_PREVIOUS_TRIPS = Menu.FIRST + 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button mvBtn = (Button)findViewById(R.id.mapViewButton);
        
        
    	mvBtn.setOnClickListener(new Button.OnClickListener()
    	{
    		public void onClick(View v) {
    			try {
    				System.out.println("Start view map");
    				Intent i = new Intent(".VIEW_MAP");
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
		menu.add(0, MainActivity.MENU_PREVIOUS_TRIPS, 0, R.string.menu_previous_trips);
		openOptionsMenu();
		return true;
	}
}