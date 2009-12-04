package itu.malta.drunkendroid.control.services;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.handlers.EventReceiver;
import itu.malta.drunkendroid.ui.activities.MoodReadActivity;
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
public class DrunkenService extends Service implements ILocationAdapterListener {

	private static DrunkenService _service = null;
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

		Log.i(this.getString(R.string.log_tag), "Service started");
		_notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		DrunkenService._service = this;
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
		_locationManager = new GPSLocationAdapter(this);
		_locationManager.RegisterLocationUpdates(this);

		_repo = new TripRepository(this);
	}

	/**
	 * Static method to get the Singleton instance.
	 */
	public static DrunkenService getInstance() {
		return _service;
	}

	/**
	 * Returns the TripRepository attached to the service.
	 */
	public TripRepository getRepository() {
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
				if (_repo.hasActiveTrip()) {
					toast = Toast.makeText(this, "Continuing old trip!", 7);
					toast.show();
				} else {
					toast = Toast.makeText(this, "Trip started!", 5);
					toast.show();
				}
				break;
			case SERVICE_COMMAND_END_TRIP:
				_repo.endTrip();
				toast = Toast.makeText(this, "Trip ended!", 5);
				toast.show();
				stopSelf();
				break;
			}
		} else {
			// No arguments, assume that service has been restarted by the
			// Android
			// framework.
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
		_repo.closeRepository();
		_locationManager.UnregisterLocationUpdates(this);
		_moodHandler.removeMessages(0);
		DrunkenService._service = null;
		_notificationManager.cancel(1);
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
	private void StartReadingTimer(final SharedPreferences sp) {
		final Intent moodIntent = new Intent(DrunkenService.this,
				MoodReadActivity.class);
		String[] intervalArray = getResources().getStringArray(
				R.array.mood_read_intervals);
		int selectedIndex = sp.getInt("moodReadInterval", 0);
		if (selectedIndex >= 0 && selectedIndex < 5) {
			_readingInterval = Integer.parseInt(intervalArray[sp.getInt(
					"moodReadInterval", 0)]);
			Runnable run = new Runnable() {
				public void run() {
					System.out.println("MoodRead Intervallet sat til "
							+ _readingInterval);
					_moodHandler.postDelayed(this, _readingInterval * 60000);
					Notification not = new Notification(R.drawable.icon,
							"Time for a new Mood Reading!", System
									.currentTimeMillis());
					not.flags = Notification.FLAG_AUTO_CANCEL;
					not.defaults |= Notification.DEFAULT_SOUND;
					not.vibrate = new long[] { 0, 1000, 2000, 3000 };
					not.setLatestEventInfo(DrunkenService.this,
							"How are you feeling?",
							"Click here to make a new Mood Reading!", PendingIntent.getActivity(DrunkenService.this, 0,
									moodIntent, 0));
					_notificationManager.notify(1, not);
					
				}
			};
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
		Toast t = Toast.makeText(DrunkenService.getInstance(), "GPS update 2, Accuracy:" + location.getAccuracy(), 8);
		t.show();
		
		Intent i = new Intent("itu.malta.drunkendroid.NEW_LOCATION_CHANGE");
		i.putExtra("location", location);
		sendBroadcast(i);
	}
}
