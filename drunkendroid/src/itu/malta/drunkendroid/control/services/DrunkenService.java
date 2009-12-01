package itu.malta.drunkendroid.control.services;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.control.TripRepository;
import itu.malta.drunkendroid.domain.LocationEvent;
import itu.malta.drunkendroid.handlers.EventHandler;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class DrunkenService extends Service implements ILocationAdapterListener {

	private static DrunkenService _service = null;
	private Handler _moodHandler = new Handler();
	private EventHandler _eventHandler = new EventHandler();
	private SMSObserver _smsObserver = new SMSObserver(new Handler());
	private PhoneStateListener _callHandler = new CallListener();
	private TelephonyManager _phoneManager;
	private int _readingInterval;
	private ILocationAdapter _locationManager;
	private TripRepository _repo;
	public final static int SERVICE_COMMAND_START_TRIP = 1;
	public final static int SERVICE_COMMAND_END_TRIP = 2;

	@Override
	public void onCreate() {
		super.onCreate();

		System.out.println("Service Started");

		DrunkenService._service = this;
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
		_locationManager = new GPSLocationAdapter(this);
		_locationManager.RegisterLocationUpdates(this);

		_repo = new TripRepository(this);
	}

	public static DrunkenService getInstance() {
		return _service;
	}

	public TripRepository getRepository() {
		return _repo;
	}

	public Location getLastKnownLocation() {
		return _locationManager.GetLastKnownLocation();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent.getExtras() != null) {
			Toast toast;
			switch (intent.getExtras().getInt("command")) {
			case SERVICE_COMMAND_START_TRIP:
				toast = Toast.makeText(this, "Trip started!", 5);
				toast.show();
				break;
			case SERVICE_COMMAND_END_TRIP:
				_repo.endTrip();
				toast = Toast.makeText(this, "Trip ended!", 5);
				toast.show();
				stopSelf();
				break;
			}
		} else {
			// No arguments, assume that service has been restarted by Android
			// framework.
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service stopped");
		UnregisterReceivers();
		_repo.closeRepository();
		_locationManager.UnregisterLocationUpdates(this);
		_moodHandler.removeMessages(0);
		DrunkenService._service = null;
	}

	/**
	 * Register BroadcastReceivers to Service, and connect as listener to
	 * SharePreferences.
	 */
	public void RegisterReceivers() {
		IntentFilter smsFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		this.registerReceiver(_eventHandler, smsFilter);

		IntentFilter moodReadingFilter = new IntentFilter("NEW_MOOD_READING");
		moodReadingFilter
				.addCategory("itu.malta.drunkendroid.control.services");
		this.registerReceiver(_eventHandler, moodReadingFilter);

		IntentFilter callFilter = new IntentFilter(
				"android.intent.action.NEW_OUTGOING_CALL");
		this.registerReceiver(_eventHandler, callFilter);

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

	public void UnregisterReceivers() {
		this.unregisterReceiver(this._eventHandler);
		_phoneManager.listen(_callHandler, PhoneStateListener.LISTEN_NONE);
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
		_readingInterval = Integer.parseInt(intervalArray[sp.getInt(
				"moodReadInterval", 0)]);
		Runnable run = new Runnable() {
			public void run() {
				System.out.println("MoodRead Intervallet sat til "
						+ _readingInterval);
				_moodHandler.postDelayed(this, _readingInterval * 60000);
			}
		};
		_moodHandler.removeMessages(0);
		_moodHandler.postDelayed(run, _readingInterval * 60000);
		System.out.println("Interval sat til " + _readingInterval);
	}

	private class SMSObserver extends ContentObserver {

		public SMSObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			_eventHandler.handleOutgoingSMS(DrunkenService.this);
		}
	}

	private class CallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			if (state == android.telephony.TelephonyManager.CALL_STATE_RINGING) {
				_eventHandler.handleIncomingCall(incomingNumber);
			}
		}
	}

	public void OnLocationChange(Location location) {
		// The location of the device has changed.
		_eventHandler.handleLocationChange(location);
	}
}
