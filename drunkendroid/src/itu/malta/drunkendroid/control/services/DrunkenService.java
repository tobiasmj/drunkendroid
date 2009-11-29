package itu.malta.drunkendroid.control.services;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.domain.ReadingEvent;
import itu.malta.drunkendroid.handlers.SMSHandler;
import itu.malta.drunkendroid.ui.activities.MainActivity;
import itu.malta.drunkendroid.ui.activities.MoodReadActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class DrunkenService extends Service implements
		ILocationAdapterListener {

	private static DrunkenService drunkenService = null;
	private Handler moodReadHandler = new Handler();
	private SMSHandler smsHandler = new SMSHandler();
	private SMSReceiver SMSReceiver;
	private SMSObserver smsObserver = new SMSObserver(new Handler());
	private PhoneStateListener callHandler = new CallListener();
	private TelephonyManager phoneManager;
	private MoodReadingReceiver moodReadingReceiver;
	private int readingInterval;
	private ILocationAdapter manager;
	private TripRepository repository;
	public final static int SERVICE_COMMAND_START_TRIP = 1;
	public final static int SERVICE_COMMAND_END_TRIP = 2;

	@Override
	public void onCreate() {
		super.onCreate();

		System.out.println("Service Started");

		DrunkenService.drunkenService = this;
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
		manager = new GPSLocationAdapter(this);
		manager.RegisterLocationUpdates(this);
		 
		repository = new TripRepository(this);
	}	
	
	public static DrunkenService getInstance() {
		return drunkenService;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if(intent.getExtras()!=null) {
			Toast toast;
			switch(intent.getExtras().getInt("command")) {
				case SERVICE_COMMAND_START_TRIP:
					toast = Toast.makeText(this, "Trip started!", 5);
					toast.show();
					break;
				case SERVICE_COMMAND_END_TRIP:
					repository.endTrip();
					toast = Toast.makeText(this, "Trip ended!", 5);
					toast.show();
					stopSelf();
					break;
			}
		}	
		else
		{
			//No arguments, assume that service has been restarted by Android framework.
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("Service stopped");
		UnregisterReceivers();
		repository.closeRepository();
		manager.UnregisterLocationUpdates(this);
		moodReadHandler.removeMessages(0);
		DrunkenService.drunkenService = null;
	}

	/**
	 * Register BroadcastReceivers to Service, and connect as listener to
	 * SharePreferences.
	 */
	public void RegisterReceivers() {
		// Register receiver to trigger when SMS is received
		BroadcastReceiver intentHandler = new EventReceiver();
		
		IntentFilter SMSFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
		IntentFilter locationFilter = new IntentFilter("android.intent.action.LOCATION_CHANGED");
		IntentFilter moodReadingFilter = new IntentFilter("NEW_MOOD_READING");
		
		this.registerReceiver(intentHandler, SMSFilter);
		this.registerReceiver(intentHandler, locationFilter);
		this.registerReceiver(intentHandler, moodReadingFilter);

		// Register as listener to receive changes in Mood Reading Interval.
		SharedPreferences prefs = getSharedPreferences("prefs_config",
				MODE_PRIVATE);
		prefs
				.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {

					public void onSharedPreferenceChanged(SharedPreferences sp,
							String key) {

						// if changes are made to the interval, change the timer
						// of the mood readings.
						if (key.equals("moodReadInterval"))
							StartReadingTimer(sp);
					}
				});

		ContentResolver contentResolver = getContentResolver();
		contentResolver.registerContentObserver(Uri.parse("content://sms"),
				true, smsObserver);

		phoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneManager.listen(callHandler, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void UnregisterReceivers() {
		this.unregisterReceiver(this.SMSReceiver);
		this.unregisterReceiver(this.moodReadingReceiver);
		phoneManager.listen(callHandler, PhoneStateListener.LISTEN_NONE);
	}

	/**
	 * Start a timer for showing the Moodread Dialog at a specified interval.
	 * 
	 * @param sp
	 *            SharedPreferences containing the mood reading interval.
	 */
	private void StartReadingTimer(final SharedPreferences sp) {
		String[] intervalArray = getResources().getStringArray(
				R.array.mood_read_intervals);
		readingInterval = Integer.parseInt(intervalArray[sp.getInt(
				"moodReadInterval", 0)]);
		Runnable run = new Runnable() {
			public void run() {
				System.out.println("MoodRead Intervallet sat til "
						+ readingInterval);
				
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				PendingIntent pi = PendingIntent.getActivity(DrunkenService.this, 0, new Intent(DrunkenService.this, MainActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
				Notification notification = new Notification(R.drawable.icon, "New MoodReading Pending", System.currentTimeMillis());
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.setLatestEventInfo(DrunkenService.this, "DrunkenDroid MoodReading", "You should complete a MoodReading!", pi);
				mNotificationManager.notify(1, notification);
				
				Intent i = new Intent(DrunkenService.this, MoodReadActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				startActivity(i);
				moodReadHandler.postDelayed(this, readingInterval * 60000);
			}
		};
		moodReadHandler.removeMessages(0);
		moodReadHandler.postDelayed(run, readingInterval * 60000);
		System.out.println("Interval sat til " + readingInterval);
	}

	private class SMSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("SMS received!");
			Message msg = new Message();
			msg.arg1 = 1;
			msg.obj = intent;
			smsHandler.sendMessage(msg);
		}
	};

	private class EventReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context cont, Intent intent) {
			String action = intent.getAction();
			
			if(action == "android.intent.action.DATA_SMS_RECEIVED")
				HandleSMS(intent);
			if(action == "android.intent.action.LOCATION_CHANGED")
				HandleLocationChange(intent);
			if(action == "MOOD_READING")
				HandleMoodReading(intent);
			
		}

		private void HandleMoodReading(Intent intent) {
			final Bundle bundle = intent.getExtras();

			if (bundle != null && bundle.getShort("mood") != 0) {
				System.out.println("Mood Reading Received by Service!");
				Thread t = new Thread() {
					@Override
					public void run() {
						Location location = manager.GetLastKnownLocation();
						ReadingEvent readingEvent = new ReadingEvent(location,bundle.getShort("mood"));
						repository.addEvent(readingEvent);
						System.out.println("Sending MoodReading : " + location.getLatitude() + " x " +
								location.getLongitude());
					}
				};

				t.start();

			} else {
				throw new IllegalArgumentException(
						"Intent contains no or invalid data!");
			}			
		}

		private void HandleLocationChange(Intent intent) {
			// The location of the device has changed. Save event to trip and check
			// for possible events with unset locations.
			
			
			
			Location location = manager.GetLastKnownLocation();
			Toast toast = Toast.makeText(DrunkenService.this, "Location changed! Accuracy: " + location.getAccuracy(), 5);
			toast.show();
			LocationEvent locationEvent = new LocationEvent(location);
			repository.updateEventsWithoutLocation(location);
			repository.addEvent(locationEvent);
		}

		private void HandleSMS(Intent intent) {
			System.out.println("SMS received!");
			Message msg = new Message();
			msg.arg1 = 1;
			msg.obj = intent;
			smsHandler.sendMessage(msg);
		}
		
	}
	
	private class MoodReadingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			final Bundle bundle = intent.getExtras();

			if (bundle != null && bundle.getShort("mood") != 0) {
				System.out.println("Mood Reading Received by Service!");
				Thread t = new Thread() {
					@Override
					public void run() {
						Location location = manager.GetLastKnownLocation();
						ReadingEvent readingEvent = new ReadingEvent(location,bundle.getShort("mood"));
						repository.addEvent(readingEvent);
						System.out.println("Sending MoodReading : " + location.getLatitude() + " x " +
								location.getLongitude());
					}
				};

				t.start();

			} else {
				throw new IllegalArgumentException(
						"Intent contains no or invalid data!");
			}
		}
	};

	private class SMSObserver extends ContentObserver {

		public SMSObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			Uri uriSMSURI = Uri.parse("content://sms");
			Cursor cur = getContentResolver().query(uriSMSURI, null, null,
					null, null);
			cur.moveToNext();
			String protocol = cur.getString(cur.getColumnIndex("protocol"));
			if (protocol == null)
				System.out.println("SMS afsendt!");
		}
	}

	private class CallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			if (state == android.telephony.TelephonyManager.CALL_STATE_RINGING) {
				// Call trip repository with a new CallEvent.
			}
		}
	}

	public void OnLocationChange(Location location) {
		// The location of the device has changed. Save event to trip and check
		// for possible events with unset locations.
		Toast toast = Toast.makeText(this, "Location changed! Accuracy: " + location.getAccuracy(), 5);
		toast.show();
		LocationEvent locationEvent = new LocationEvent(location);
		repository.updateEventsWithoutLocation(location);
		repository.addEvent(locationEvent);
	}
}
