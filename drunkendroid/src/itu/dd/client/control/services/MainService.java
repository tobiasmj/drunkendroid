package itu.dd.client.control.services;

import itu.dd.client.control.EventReceiver;
import itu.dd.client.control.TripRepository;
import itu.dd.client.ui.activities.MoodReadActivity;
import itu.dd.client.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Service singleton running in background when Trip is ongoing. Service is
 * destroyed as soon as a trip has ended. *
 */
public class MainService extends Service implements ILocationAdapterListener {

	private static MainService _service = null;
	private Handler _moodHandler = new Handler();
	private EventReceiver _eventHandler = new EventReceiver();
	private SMSObserver _smsObserver = new SMSObserver(new Handler());
	private PhoneStateListener _callHandler = new CallListener();
	private SharedPreferences.OnSharedPreferenceChangeListener _prefsListener;
	private TelephonyManager _phoneManager;
	private int _readingInterval;
	private ILocationAdapter _locationManager;
	private TripRepository _repo;
	private NotificationManager _notificationManager;
	public final static int SERVICE_COMMAND_START_TRIP = 1;
	public final static int SERVICE_COMMAND_END_TRIP = 2;

	/**
	 * First method to be called when creating servce. Sets up the service as a
	 * Singleton.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		MainService._service = this;
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		_locationManager = new GPSLocationAdapter(this);
		_locationManager.RegisterLocationUpdates(this);
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
		Log.i(this.getString(R.string.log_tag), "Service started");

	}

	/**
	 * Static method to get the Singleton instance.
	 */
	public static MainService getInstance() {
		return _service;
	}

	/**
	 * Returns the TripRepository attached to the service.
	 */
	public TripRepository getRepository() {
		if(_service != null && _repo == null)
			_repo = new TripRepository(this);
		return _repo;
	}

	/**
	 * Returns the last known location from the ILocationAdapter.
	 */
	public Location getLastKnownLocation() {
		return _locationManager.GetLastKnownLocation();
	}

	/**
	 * Method called every time startService() is called. Contains service
	 * commands if called from application, otherwise the service has been
	 * restarted by the Android framework.
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent.getExtras() != null) {
			Toast toast;
			switch (intent.getExtras().getInt("command")) {
			case SERVICE_COMMAND_START_TRIP:
				String name = intent.getExtras().getString("name");
				_repo = new TripRepository(this, name);
				if (getRepository().hasActiveTrip()) {
					toast = Toast.makeText(this, "Continuing old trip!", 7);
					toast.show();
				} else {
					toast = Toast.makeText(this, "Trip started!", 5);
					toast.show();
				}
				break;
			case SERVICE_COMMAND_END_TRIP:
				getRepository().endTrip();
				toast = Toast.makeText(this, "Trip ended!", 5);
				toast.show();
				stopSelf();
				break;
			}
		} else {
			// No arguments, assume that service has been restarted by the
			// Android framework.
		}
	}	

	/**
	 * Method called if a client tries to bind itself to the service.
	 * 
	 * @return Returns null since the service does not allow clients.
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Called when the service is destroyed, either by the application or by the
	 * Android Framework. Unregisters receivers and cleans up.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(this.getString(R.string.log_tag), "Service stopped");
		UnregisterReceivers();
		getRepository().closeRepository();
		_locationManager.UnregisterLocationUpdates(this);
		_notificationManager.cancel(1);
		_moodHandler.removeMessages(0);
		MainService._service = null;
	}

	/**
	 * Start listening for Intents and content changes.
	 */
	private void RegisterReceivers() {
		IntentFilter moodReadingFilter = new IntentFilter(
				"itu.malta.drunkendroid.NEW_MOOD_READING");
		this.registerReceiver(_eventHandler, moodReadingFilter);

		IntentFilter locationChangeFilter = new IntentFilter(
				"itu.malta.drunkendroid.NEW_LOCATION_CHANGE");
		this.registerReceiver(_eventHandler, locationChangeFilter);

		IntentFilter outgoingCallFilter = new IntentFilter(
				"android.intent.action.NEW_OUTGOING_CALL");
		this.registerReceiver(_eventHandler, outgoingCallFilter);

		IntentFilter incomingCallFilter = new IntentFilter(
				"itu.malta.drunkendroid.NEW_INCOMING_CALL");
		this.registerReceiver(_eventHandler, incomingCallFilter);

		IntentFilter outgoingSMSFilter = new IntentFilter(
				"itu.malta.drunkendroid.NEW_OUTGOING_SMS");
		this.registerReceiver(_eventHandler, outgoingSMSFilter);

		IntentFilter incomingSMSFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		this.registerReceiver(_eventHandler, incomingSMSFilter);

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
				true, _smsObserver);

		_phoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		_phoneManager
				.listen(_callHandler, PhoneStateListener.LISTEN_CALL_STATE);

	}

	/**
	 * Stop listening for Intents and content changes.
	 */
	private void UnregisterReceivers() {
		this.unregisterReceiver(this._eventHandler);
		_phoneManager.listen(_callHandler, PhoneStateListener.LISTEN_NONE);
		getContentResolver().unregisterContentObserver(_smsObserver);
		SharedPreferences prefs = getSharedPreferences("prefs_config",
				MODE_PRIVATE);
		prefs.unregisterOnSharedPreferenceChangeListener(_prefsListener);
	}

	/**
	 * Start a timer in order to show the Mood Reading Dialog at an interval
	 * specified in the application preferences.
	 * 
	 * @param sp
	 *            SharedPreferences containing the mood reading interval.
	 */
	private void StartReadingTimer(SharedPreferences sp) {
		final Intent moodIntent = new Intent(MainService.this,
				MoodReadActivity.class);
		String[] intervalArray = getResources().getStringArray(
				R.array.mood_read_intervals);
		int selectedIndex = sp.getInt("moodReadInterval", 0);
		if (selectedIndex >= 0 && selectedIndex < 5) {
			Runnable run = new Runnable() {
				public void run() {
					System.out.println("MoodRead Intervallet sat til "
							+ _readingInterval);
					_moodHandler.postDelayed(this, _readingInterval * 60000);
					Notification not = new Notification(R.drawable.notification_icon,
							"Time for a new Mood Reading!", System
									.currentTimeMillis());
					not.flags = Notification.FLAG_AUTO_CANCEL;
					not.defaults |= Notification.DEFAULT_SOUND;
					not.vibrate = new long[] { 0, 1000, 2000, 3000 };
					not.setLatestEventInfo(MainService.this,
							"How are you feeling?",
							"Click here to make a new Mood Reading!", PendingIntent.getActivity(MainService.this, 0,
									moodIntent, 0));
					_notificationManager.notify(1, not);
					
				}
			};
			_readingInterval = Integer.parseInt(intervalArray[sp.getInt(
					"moodReadInterval", 0)]);
			
			_moodHandler.removeMessages(0);
			_moodHandler.postDelayed(run, _readingInterval * 60000);
		}
	}

	/**
	 * Content observer for listening to changes in the SMS directory. Fires a
	 * NEW_OUTGOING_SMS intent on changes.
	 */
	private class SMSObserver extends ContentObserver {

		public SMSObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			sendBroadcast(new Intent("itu.malta.drunkendroid.NEW_OUTGOING_SMS"));
		}
	}

	/**
	 * PhoneStateListener for listening to changes in phone state. Fires a
	 * NEW_INCOMING_CALL intent on phone state change.
	 */
	private class CallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			// Fire intent only on incoming call.
			if (state == android.telephony.TelephonyManager.CALL_STATE_RINGING) {
				Intent i = new Intent(
						"itu.malta.drunkendroid.NEW_INCOMING_CALL");
				i.putExtra("phoneNumber", incomingNumber);
				sendBroadcast(i);
			}
		}
	}

	/**
	 * Interface method called by ILocationAdapter if registered to such.
	 */
	public void OnLocationChange(Location location) {
		Intent i = new Intent("itu.malta.drunkendroid.NEW_LOCATION_CHANGE");
		i.putExtra("location", location);
		sendBroadcast(i);
	}
}
