package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MoodReadActivity extends Activity {

	/**
	 * Called when the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moodreading);
		
		SeekBar seekbar = (SeekBar)findViewById(R.id.SeekBar01);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				Intent i = new Intent("itu.malta.drunkendroid.NEW_MOOD_READING");
				i.putExtra("mood", (short) seekBar.getProgress());
				sendBroadcast(i);
				MoodReadActivity.this.finish();
			}
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}
			public void onStartTrackingTouch(SeekBar seekBar) {	}
		});
		
		Button dismissButton = (Button)findViewById(R.id.dismissbutton);
		dismissButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				MoodReadActivity.this.finish();
			}
		});
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
