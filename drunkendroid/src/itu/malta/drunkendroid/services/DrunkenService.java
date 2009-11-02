package itu.malta.drunkendroid.services;

import itu.malta.drunkendroid.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class DrunkenService extends Service {
	
	private static DrunkenService drunkenService = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		System.out.println("Service Started");
		
		DrunkenService.drunkenService = new DrunkenService();
		
		IntentFilter filter = new IntentFilter("android.intent.action.TIME_TICK");
		
		final class SMSBroadcastReceiver extends BroadcastReceiver {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				System.out.println("Minute tick received!");
			}
		};
		
		registerReceiver(new SMSBroadcastReceiver(), filter);
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
		
		DrunkenService.drunkenService = null;
	}
	
	
	
	

}
