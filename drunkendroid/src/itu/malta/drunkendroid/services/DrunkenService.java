package itu.malta.drunkendroid.services;

import itu.malta.drunkendroid.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;

public class DrunkenService extends Service {
	
	private static DrunkenService drunkenService = null;
	Handler moodReadHandler = new Handler();
	public SMSReceiver SMSReceiver;
	int readingInterval;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		System.out.println("Service Started");
		
		DrunkenService.drunkenService = new DrunkenService();
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
	}

	public static DrunkenService getInstance()
	{
		return drunkenService;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("Service stopped");
		UnregisterReceivers();
		DrunkenService.drunkenService = null;
	}

	public void RegisterReceivers()
	{
		IntentFilter filter = new IntentFilter("android.intent.action.TIME_TICK");
		this.SMSReceiver = new SMSReceiver();
		this.registerReceiver(this.SMSReceiver, filter);
		
		SharedPreferences prefs = getSharedPreferences("prefs_config", MODE_PRIVATE);
		prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
			
			public void onSharedPreferenceChanged(SharedPreferences sp,
					String key) {
				
				if(key.equals("moodReadInterval"))
					StartReadingTimer(sp);
			}
		});
	}
	
	public void UnregisterReceivers()
	{
		this.unregisterReceiver(this.SMSReceiver);
	}
	
	private void StartReadingTimer(final SharedPreferences sp)
	{		
		String[] intervalArray = getResources().getStringArray(R.array.mood_read_intervals);
		readingInterval = Integer.parseInt(intervalArray[sp.getInt("moodReadInterval", 0)]);
		Runnable run = new Runnable() {
			public void run() 
			{
				moodReadHandler.postDelayed(this, readingInterval*60000);
				System.out.println("MoodRead Intervallet sat til " + readingInterval);
			}
		};
		moodReadHandler.removeMessages(0);
		moodReadHandler.postDelayed(run, readingInterval*60000);
	}
	
	private class SMSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			System.out.println("Minute tick received!");
		}
	};
}
