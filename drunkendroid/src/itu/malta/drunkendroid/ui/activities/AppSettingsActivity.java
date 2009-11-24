package itu.malta.drunkendroid.ui.activities;

import itu.malta.drunkendroid.R;
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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class AppSettingsActivity extends Activity {
	
	private Spinner moodReadSpinner;
	private CheckBox registerVolumeCheckBox;
	private static final int SAVE_SETTINGS = Menu.FIRST;
	private static final int RESTORE_SETTINGS = Menu.FIRST+1;
	private static final int DISCARD_SETTINGS = Menu.FIRST+2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appsettings);

		moodReadSpinner = (Spinner)this.findViewById(R.id.MoodReadIntervalSpinner);
		ArrayAdapter<CharSequence> moodReadAdapter = ArrayAdapter.createFromResource(this, R.array.mood_read_intervals, R.layout.custom_bright_spinner);
		moodReadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		moodReadSpinner.setAdapter(moodReadAdapter);
		moodReadSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			    view.setBackgroundColor(android.R.color.transparent);
			  }
			  public void onNothingSelected(AdapterView<?> parent) {}
			}
		); 
		
		registerVolumeCheckBox = (CheckBox) this.findViewById(R.id.RegisterVolumeCheckBox);
		
		Button saveButton = (Button)this.findViewById(R.id.SaveSettingsButton);
		saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SaveChanges();
				finish();
			}
		});
		
		Button discardButton = (Button)this.findViewById(R.id.DiscardSettingsButton);
		discardButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		Setup();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, AppSettingsActivity.SAVE_SETTINGS, 0, R.string.save_settings_button).setIcon(android.R.drawable.ic_menu_save);
		menu.add(0, AppSettingsActivity.DISCARD_SETTINGS, 0, R.string.discard_settings_button).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menu.add(0, AppSettingsActivity.RESTORE_SETTINGS, 0, R.string.restore_settings_button).setIcon(android.R.drawable.ic_menu_delete);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {		
		switch(item.getItemId()) {
			case AppSettingsActivity.SAVE_SETTINGS:
				SaveChanges();
				finish();
				break;
			case AppSettingsActivity.DISCARD_SETTINGS:
				finish();
				break;
			case AppSettingsActivity.RESTORE_SETTINGS:
				RestoreDefaults();
				
			return true;	
		}		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void Setup()
	{
		TextView firstTimeLayer = (TextView)this.findViewById(R.id.FirstTimeUseLayout);
		
		SharedPreferences prefs = getSharedPrefs();
		
		//Gets sharedpreferences showing whether the application has been configured or not.
		if(prefs.getBoolean("isConfigured", false))
			firstTimeLayer.setVisibility(View.GONE);
		else 	
			firstTimeLayer.setVisibility(View.VISIBLE);
		
		moodReadSpinner.setSelection(prefs.getInt("moodReadInterval", 0));
		
		if(prefs.getBoolean("registerVolume", true))
			registerVolumeCheckBox.setChecked(true);
		else
			registerVolumeCheckBox.setChecked(false);
	}
	
	private void SaveChanges() 
	{
		SharedPreferences.Editor prefsEditor = getSharedPrefs().edit();
		prefsEditor.putInt("moodReadInterval", (int)moodReadSpinner.getSelectedItemId());
		prefsEditor.putBoolean("registerVolume", registerVolumeCheckBox.isChecked());
		prefsEditor.putBoolean("isConfigured", true);
		prefsEditor.commit();
	}
	
	private void RestoreDefaults() 
	{
	    new AlertDialog.Builder(this)
	      .setMessage("Restore defaults?")
	      .setPositiveButton("Yes", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				moodReadSpinner.setSelection(0);
				registerVolumeCheckBox.setChecked(true);
			}
		}).setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		}).show();
	}
	
	private SharedPreferences getSharedPrefs()
	{
		return this.getSharedPreferences("prefs_config", MODE_PRIVATE);
	}
}
