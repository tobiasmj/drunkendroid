package itu.malta.drunkendroid.handlers;

import itu.malta.drunkendroid.control.services.DrunkenService;
import itu.malta.drunkendroid.domain.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class EventReceiver extends BroadcastReceiver {
	public static final int EVENT_MOOD_READING = 1;
	public static final int EVENT_LOCATION_CHANGE = 2;
	public static final int EVENT_OUTGOING_CALL = 3;
	public static final int EVENT_INCOMING_CALL = 4;
	public static final int EVENT_OUTGOING_SMS = 5;
	public static final int EVENT_INCOMING_SMS = 6;

	@Override
	public void onReceive(Context context, Intent intent) {

		Message m = new Message();
		m.obj = intent;

		if (intent.getAction().equals("NEW_MOOD_READING")) {
			m.arg1 = EVENT_MOOD_READING;
			Log.i("DrunkDroid", "Incoming MoodReading");
		} else if (intent.getAction().equals("NEW_LOCATION_CHANGE")) {
			m.arg1 = EVENT_LOCATION_CHANGE;
			Log.i("DrunkDroid", "Location change");
		} else if (intent.getAction().equals(
				"android.intent.action.NEW_OUTGOING_CALL")) {
			m.arg1 = EVENT_OUTGOING_CALL;
			Log.i("DrunkDroid", "Outgoing Call");
		} else if (intent.getAction().equals("NEW_INCOMING_CALL")) {
			m.arg1 = EVENT_INCOMING_CALL;
			Log.i("DrunkDroid", "Incoming Call");
		} else if (intent.getAction().equals("NEW_OUTGOING_SMS")) {
			m.arg1 = EVENT_OUTGOING_SMS;
			Log.i("DrunkDroid", "Outgoing SMS");
		} else if (intent.getAction().equals(
				"android.provider.Telephony.SMS_RECEIVED")) {
			m.arg1 = EVENT_INCOMING_SMS;
			Log.i("DrunkDroid", "Incoming SMS");
		}

		EventHandler t = new EventHandler(m);
		t.start();
	}

	private class EventHandler extends Thread {
		Message _msg;

		public EventHandler(Message msg) {
			_msg = msg;
		}

		public void run() {
			switch (_msg.arg1) {
			case EVENT_MOOD_READING:
				//HandleMoodReading
				Intent i = (Intent)_msg.obj;
			}
		}

		private void handleOutgoingCall(Intent intent) {
			OutgoingCallEvent event = new OutgoingCallEvent(DrunkenService
					.getInstance().getLastKnownLocation(), intent.getExtras()
					.getString(Intent.EXTRA_PHONE_NUMBER));
			DrunkenService.getInstance().getRepository().addEvent(event);
		}

		private void HandleMoodReading(Intent i) {
			final Bundle bundle = i.getExtras();

			if (bundle != null && bundle.getShort("mood") != 0) {
				System.out.println("Mood Reading Received by Service!");
				Location location = DrunkenService.getInstance()
						.getLastKnownLocation();
				ReadingEvent readingEvent = new ReadingEvent(location, bundle
						.getShort("mood"));
				DrunkenService.getInstance().getRepository().addEvent(
						readingEvent);
				System.out.println("Sending MoodReading : "
						+ location.getLatitude() + " x "
						+ location.getLongitude());
			} else {
				throw new IllegalArgumentException(
						"Intent contains no or invalid data!");
			}
		}

		private void HandleIncomingSMS(Intent intent) {
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];

				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				String message = "";
				for (SmsMessage m : messages) {
					message += m.getMessageBody();
				}

				IncomingSMSEvent event = new IncomingSMSEvent(DrunkenService
						.getInstance().getLastKnownLocation(), messages[0]
						.getOriginatingAddress(), message);
				DrunkenService.getInstance().getRepository().addEvent(event);
			}
		}
	}

	public void handleIncomingCall(final String phoneNumber) {
		Log.i("DrunkDroid", "Incoming Call");
		Thread t = new Thread() {
			@Override
			public void run() {
				// TODO Get name from phonebook and call duration
				IncomingCallEvent event = new IncomingCallEvent(DrunkenService
						.getInstance().getLastKnownLocation(), phoneNumber);
				DrunkenService.getInstance().getRepository().addEvent(event);
			}
		};
		t.start();
	}

	public void handleOutgoingSMS(final Context context) {
		Thread t = new Thread() {
			@Override
			public void run() {
				Uri smsUri = Uri.parse("content://sms");
				String orderBy = "date desc";
				Cursor cur = context.getContentResolver().query(smsUri, null,
						null, null, orderBy);
				if (cur.moveToNext()) {
					if (cur.getString(cur.getColumnIndex("type")).equals("2")) {
						Log.i("DrunkDroid", "Outgoing SMS");
						String phoneNumber = cur.getString(cur
								.getColumnIndex("address"));
						String message = cur.getString(cur
								.getColumnIndex("body"));
						OutgoingSMSEvent event = new OutgoingSMSEvent(
								DrunkenService.getInstance()
										.getLastKnownLocation(), phoneNumber,
								message);
						DrunkenService.getInstance().getRepository().addEvent(
								event);
					}
				}
			}
		};
		t.start();
	}

	public void handleLocationChange(final Location location) {
		Log.i("DrunkDroid", "Incoming LocationChange");
		Thread t = new Thread() {
			@Override
			public void run() {
				LocationEvent locationEvent = new LocationEvent(location);
				// Save event to trip and check for possible events with empty
				// locations.
				DrunkenService.getInstance().getRepository()
						.updateEventsWithoutLocation(location);
				DrunkenService.getInstance().getRepository().addEvent(
						locationEvent);
			}
		};
		t.start();
	}
}