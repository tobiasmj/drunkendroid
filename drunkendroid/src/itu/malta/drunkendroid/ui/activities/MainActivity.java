package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.services.DrunkenService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;

public class MainActivity extends Activity {

	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_PREVIOUS_TRIPS = Menu.FIRST + 1;
	private static final int TRIP_STATE_RUNNING = 10;
	private static final int TRIP_STATE_NOT_RUNNING = 11;
	SlidingDrawer slider;
	ImageView startServiceBtn;
	ImageView stopServiceBtn;
	RelativeLayout moodReading;
	RelativeLayout _programGuide;
	View.OnClickListener buttonListener = new MyOnClickListener();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Setup();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(DrunkenService.getInstance() == null)
			setTripState(TRIP_STATE_NOT_RUNNING);
		else
			setTripState(TRIP_STATE_RUNNING);
	}

	private void Setup() {
		moodReading = (RelativeLayout) this.findViewById(R.id.MoodReadingLayout);
		
		SeekBar seekBar = (SeekBar) findViewById(R.id.SeekBar01);
		seekBar.setOnSeekBarChangeListener(new MySeekbarListener());		
		
		slider = (SlidingDrawer) this.findViewById(R.id.moodSlider);
		slider.open();

		final Button newReadingBtn = (Button) findViewById(R.id.NewReadingBtn);
		newReadingBtn.setOnClickListener(buttonListener);

		startServiceBtn = (ImageView) findViewById(R.id.startServiceBtn);
		startServiceBtn.setOnClickListener(buttonListener);

		stopServiceBtn = (ImageView) findViewById(R.id.stopServiceBtn);
		stopServiceBtn.setOnClickListener(buttonListener);

		_programGuide = (RelativeLayout) findViewById(R.id.info_layout);

		final ImageView mvBtn = (ImageView) findViewById(R.id.mapViewBtn);
		mvBtn.setOnClickListener(buttonListener);
	}
	
	private void setTripState(int mode) {
		switch (mode) {
		case TRIP_STATE_RUNNING:
			startServiceBtn.setVisibility(View.GONE);
			moodReading.setVisibility(RelativeLayout.VISIBLE);
			stopServiceBtn.setVisibility(View.VISIBLE);
			_programGuide.setVisibility(RelativeLayout.GONE);
			break;
		case TRIP_STATE_NOT_RUNNING:
			startServiceBtn.setVisibility(View.VISIBLE);
			moodReading.setVisibility(RelativeLayout.GONE);
			stopServiceBtn.setVisibility(View.GONE);
			_programGuide.setVisibility(RelativeLayout.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MainActivity.MENU_SETTINGS, 0, R.string.menu_settings)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MainActivity.MENU_PREVIOUS_TRIPS, 0,
				R.string.menu_previous_trips).setIcon(
				android.R.drawable.ic_menu_mapmode);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case MainActivity.MENU_SETTINGS:
			try {
				intent = new Intent("itu.malta.drunkendroid.VIEW_SETTINGS");
				startActivity(intent);
			} catch (Exception e) {
				Log.i(this.getString(R.string.log_tag),
						"Unable to fire intent:" + intent.getAction());
			}
			break;
		case MainActivity.MENU_PREVIOUS_TRIPS:
			try {
				intent = new Intent("itu.malta.drunkendroid.VIEW_PREVIOUS_TRIPS");
				startActivity(intent);
			} catch (Exception e) {
				Log.i(this.getString(R.string.log_tag),
						"Unable to fire intent:" + intent.getAction());
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class MySeekbarListener implements SeekBar.OnSeekBarChangeListener {
		public void onStopTrackingTouch(SeekBar seekBar) {
			System.out.println("Sending Mood Reading!");
			Intent i = new Intent("itu.malta.drunkendroid.NEW_MOOD_READING");
			i.putExtra("mood", (short) seekBar.getProgress());
			sendBroadcast(i);
			MainActivity.this.slider.animateClose();
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}
	}

	private class MyOnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.NewReadingBtn:
				slider.animateOpen();
				break;
			case R.id.startServiceBtn:
				try {
					Intent i = new Intent(
							MainActivity.this,
							itu.malta.drunkendroid.control.services.DrunkenService.class);
					i.putExtra("command", DrunkenService.SERVICE_COMMAND_START_TRIP);
					startService(i);
					setTripState(TRIP_STATE_RUNNING);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case R.id.stopServiceBtn:
				try {
					Intent i = new Intent(MainActivity.this, itu.malta.drunkendroid.control.services.DrunkenService.class);
					i.putExtra("command", DrunkenService.SERVICE_COMMAND_END_TRIP);
					startService(i);
					setTripState(TRIP_STATE_NOT_RUNNING);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case R.id.mapViewBtn:
				try {
					startActivity(new Intent("itu.malta.drunkendroid.VIEW_MAP"));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			}
		}

	}
}