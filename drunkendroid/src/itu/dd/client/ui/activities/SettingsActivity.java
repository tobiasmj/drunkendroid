package itu.dd.client.ui.activities;

import itu.dd.client.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SettingsActivity extends Activity {
	
	private Spinner moodReadSpinner;
	private Spinner GPSAccuracySpinner;
	private static final int CHANGES_DONE = Menu.FIRST;
	private static final int RESTORE_SETTINGS = Menu.FIRST+1;
	
	/**
	 * Called when the activity is created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appsettings);

		OnItemSelectedListener selectedListener = new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			    view.setBackgroundColor(android.R.color.transparent);
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		};
		
		moodReadSpinner = (Spinner)this.findViewById(R.id.MoodReadIntervalSpinner);
		ArrayAdapter<CharSequence> moodReadAdapter = ArrayAdapter.createFromResource(this, R.array.mood_read_intervals, R.layout.custom_bright_spinner);
		moodReadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moodReadSpinner.setAdapter(moodReadAdapter);
		moodReadSpinner.setOnItemSelectedListener(selectedListener);
		
		GPSAccuracySpinner = (Spinner)this.findViewById(R.id.GPSAccuracySpinner);
		ArrayAdapter<CharSequence> GPSAccuracyAdapter = ArrayAdapter.createFromResource(this, R.array.gps_accuracy_options, R.layout.custom_bright_spinner);
		GPSAccuracyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		GPSAccuracySpinner.setAdapter(GPSAccuracyAdapter);
		GPSAccuracySpinner.setOnItemSelectedListener(selectedListener);
		
		Button saveButton = (Button)this.findViewById(R.id.ChangesDoneButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * Called when an activity is bought back on the screen.
	 */
	@Override
	public void onResume() {
		super.onResume();
		TextView firstTimeLayer = (TextView)this.findViewById(R.id.FirstTimeUseLayout);
		
		SharedPreferences prefs = getPreferences(0);
		
		//Gets sharedpreferences showing whether the application has been configured or not.
		if(prefs.getBoolean("isConfigured", false))
			firstTimeLayer.setVisibility(View.GONE);
		else 	
			firstTimeLayer.setVisibility(View.VISIBLE);
		
		moodReadSpinner.setSelection(prefs.getInt("moodReadInterval", 1));
		GPSAccuracySpinner.setSelection(prefs.getInt("GPSAccuracy", 2));
	}
	
	/**
	 * Called when the Activity is put in the background.
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		SharedPreferences.Editor prefsEditor = getPreferences(0).edit();
		prefsEditor.putInt("moodReadInterval", (int)moodReadSpinner.getSelectedItemId());
		prefsEditor.putInt("GPSAccuracy", (int)GPSAccuracySpinner.getSelectedItemId());
		prefsEditor.putBoolean("isConfigured", true);
		prefsEditor.commit();
	}
	
	/**
	 * Called when the Options Menu is created.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, SettingsActivity.CHANGES_DONE, 0, R.string.done_changing_settings_button).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, SettingsActivity.RESTORE_SETTINGS, 0, R.string.restore_settings_button).setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}
	
	/**
	 * Called when an item in the Options Menu is selected.
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		switch(item.getItemId()) {
			case SettingsActivity.CHANGES_DONE:
				finish();
				break;
			case SettingsActivity.RESTORE_SETTINGS:
				RestoreDefaults();
				
			return true;	
		}		
		return super.onMenuItemSelected(featureId, item);
	}
	
	/**
	 * Restores the settings to the original state. Causes a confirmation dialog to be shown.
	 */
	private void RestoreDefaults() 
	{
	    new AlertDialog.Builder(this)
	      .setMessage("Restore defaults?")
	      .setPositiveButton("Yes", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				moodReadSpinner.setSelection(1);
				GPSAccuracySpinner.setSelection(2);
			}
		}).setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		}).show();
	}
}
