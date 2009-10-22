package itu.malta.drunkendroid.ui;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.R.id;
import itu.malta.drunkendroid.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        final Button mvBtn = (Button)findViewById(R.id.mapViewButton);
        
    	mvBtn.setOnClickListener(new Button.OnClickListener()
    	{
    		public void onClick(View v) {
    			// TODO Auto-generated method stub
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
}