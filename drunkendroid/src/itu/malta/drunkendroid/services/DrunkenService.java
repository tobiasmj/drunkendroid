package itu.malta.drunkendroid.services;

import itu.malta.drunkendroid.R;
import itu.malta.drunkendroid.domain.TripRepository;
import itu.malta.drunkendroid.domain.entities.Reading;
import itu.malta.drunkendroid.handlers.SMSHandler;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class DrunkenService extends Service {

	private static DrunkenService drunkenService = null;
	private Handler moodReadHandler = new Handler();
	private SMSHandler smsHandler = new SMSHandler();
	private SMSReceiver SMSReceiver;
	private SMSObserver smsObserver = new SMSObserver(new Handler());
	private MoodReadingReceiver moodReadingReceiver;
	private int readingInterval;
	private LocationManager locManager;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		System.out.println("Service Started");

		DrunkenService.drunkenService = this;
		RegisterReceivers();
		StartReadingTimer(getSharedPreferences("prefs_config", MODE_PRIVATE));
		
		
	}

	public static DrunkenService getInstance() {
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
		moodReadHandler.removeMessages(0);
		DrunkenService.drunkenService = null;
	}

	/**
	 * Register BroadcastReceivers to Service, and connect as listener to
	 * SharePreferences.
	 */
	public void RegisterReceivers() {
		// Register receiver to trigger when SMS is received
		IntentFilter filter = new IntentFilter(
				"android.intent.action.TIME_TICK");
		this.SMSReceiver = new SMSReceiver();
		this.registerReceiver(this.SMSReceiver, filter);

		// Register for incoming mood readings.
		IntentFilter moodReadingFilter = new IntentFilter(
				"NEW_MOOD_READING");
		moodReadingFilter.addCategory("itu.malta.drunkendroid");
		this.moodReadingReceiver = new MoodReadingReceiver();
		this.registerReceiver(moodReadingReceiver, moodReadingFilter);

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
	}

	public void UnregisterReceivers() {
		this.unregisterReceiver(this.SMSReceiver);
		this.unregisterReceiver(this.moodReadingReceiver);
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
			// TODO Auto-generated method stub
			System.out.println("SMS received!");
			Message msg = new Message();
			msg.arg1 = 1;
			msg.obj = intent;
			smsHandler.sendMessage(msg);
		}
	};

	private class MoodReadingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			final Bundle bundle = intent.getExtras();

			if (bundle != null && bundle.getShort("mood") != 0) {
				Thread t = new Thread() {
					@Override
					public void run() {
						TripRepository repo = new TripRepository(DrunkenService
								.getInstance());

						Reading reading = new Reading();
						reading.setMood(bundle.getShort("mood"));
						// reading.setLatitude(locManager.getLastKnownLocation(provider))
						//repo.insert(reading);
						System.out.println(reading.getMood());
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
}
