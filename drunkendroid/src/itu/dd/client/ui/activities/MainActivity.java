package itu.dd.client.ui.activities;

import itu.dd.client.control.TripRepository;
import itu.dd.client.control.services.MainService;
import itu.dd.client.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

	/**
	 * Called when the activity is created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Setup();
	}

	/**
	 * Called when the activity is brought back on the screen.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (MainService.getInstance() == null)
			setTripState(TRIP_STATE_NOT_RUNNING);
		else
			setTripState(TRIP_STATE_RUNNING);
	}

	/**
	 * Sets up the activity's buttons and views.
	 */
	private void Setup() {
		moodReading = (RelativeLayout) this
				.findViewById(R.id.MoodReadingLayout);

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

	/**
	 * Shows and hides views in the activity if a trip is running or not.
	 */
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

	/**
	 * Called when the Options Menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MainActivity.MENU_SETTINGS, 0, R.string.menu_settings)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MainActivity.MENU_PREVIOUS_TRIPS, 0,
				R.string.menu_trips).setIcon(
				android.R.drawable.ic_menu_mapmode);
		return true;
	}

	/**
	 * Called when an item in the Options Menu is selected.
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Intent intent = null;

		switch (item.getItemId()) {
		case MainActivity.MENU_SETTINGS:
			try {
				intent = new Intent("itu.dd.client.VIEW_SETTINGS");
				startActivity(intent);
			} catch (Exception e) {
				Log.i(this.getString(R.string.log_tag),
						"Unable to fire intent:" + intent.getAction());
			}
			break;
		case MainActivity.MENU_PREVIOUS_TRIPS:
			try {
				intent = new Intent(
						"itu.dd.client.VIEW_PREVIOUS_TRIPS");
				startActivity(intent);
			} catch (Exception e) {
				Log.i(this.getString(R.string.log_tag),
						"Unable to fire intent:" + intent.getAction());
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * Listener handling the interaction with the Mood Read SeekBar.
	 */
	private class MySeekbarListener implements SeekBar.OnSeekBarChangeListener {
		public void onStopTrackingTouch(SeekBar seekBar) {
			System.out.println("Sending Mood Reading!");
			Intent i = new Intent("itu.dd.client.NEW_MOOD_READING");
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

	/**
	 * Listener handling the various buttons in the activity.
	 */
	private class MyOnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.NewReadingBtn:
				slider.animateOpen();
				break;
			case R.id.startServiceBtn:
				try {
					StartTrip();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case R.id.stopServiceBtn:
				try {
					stopTrip();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case R.id.mapViewBtn:
				try {
					startActivity(new Intent("itu.dd.client.VIEW_MAP"));
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			}
		}
	}

	void StartTrip() {
		TripRepository repo = new TripRepository(this);
		if (repo.hasActiveTrip()) {
			Intent i = new Intent(
					MainActivity.this,
					itu.dd.client.control.services.MainService.class);
			i.putExtra("command", MainService.SERVICE_COMMAND_START_TRIP);
			startService(i);
			setTripState(TRIP_STATE_RUNNING);
		} else {
			final FrameLayout layout = new FrameLayout(this);
			final EditText input = new EditText(this);
			layout.addView(input, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT));
			AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
					.setView(layout).setTitle("Please name your trip")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String name = input.getText().toString();
									Intent i = new Intent(
											MainActivity.this,
											itu.dd.client.control.services.MainService.class);
									i
											.putExtra(
													"command",
													MainService.SERVICE_COMMAND_START_TRIP);
									i.putExtra("name", name);
									startService(i);
									setTripState(TRIP_STATE_RUNNING);
								}
							}).setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface d, int which) {
								}
							}).create();
			alert.show();
		}
	}

	void stopTrip() {
		AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Are you sure that you want to end the trip?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent i = new Intent(
										MainActivity.this,
										itu.dd.client.control.services.MainService.class);
								i
										.putExtra(
												"command",
												MainService.SERVICE_COMMAND_END_TRIP);
								startService(i);
								setTripState(TRIP_STATE_NOT_RUNNING);
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface d, int which) {
							}
						}).create();
		alert.show();
	}

}