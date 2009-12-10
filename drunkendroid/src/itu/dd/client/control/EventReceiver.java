package itu.dd.client.control;

import itu.dd.client.control.services.DrunkenService;
import itu.dd.client.domain.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
		
		if (intent.getAction().equals("itu.malta.drunkendroid.NEW_MOOD_READING")) {
			m.arg1 = EVENT_MOOD_READING;
		} else if (intent.getAction().equals("itu.malta.drunkendroid.NEW_LOCATION_CHANGE")) {
			m.arg1 = EVENT_LOCATION_CHANGE;
		} else if (intent.getAction().equals(
				"android.intent.action.NEW_OUTGOING_CALL")) {
			m.arg1 = EVENT_OUTGOING_CALL;
		} else if (intent.getAction().equals("itu.malta.drunkendroid.NEW_INCOMING_CALL")) {
			m.arg1 = EVENT_INCOMING_CALL;
		} else if (intent.getAction().equals("itu.malta.drunkendroid.NEW_OUTGOING_SMS")) {
			m.arg1 = EVENT_OUTGOING_SMS;
		} else if (intent.getAction().equals(
				"android.provider.Telephony.SMS_RECEIVED")) {
			m.arg1 = EVENT_INCOMING_SMS;
		}

		EventHandler t = new EventHandler(m);
		t.start();
	}

	protected class EventHandler extends Thread {
		Message _msg;

		public EventHandler(Message msg) {
			_msg = msg;
		}

		public void run() {
			Intent i = (Intent) _msg.obj;
			switch (_msg.arg1) {
			case EVENT_MOOD_READING:
				handleMoodReading(i);
				break;
			case EVENT_LOCATION_CHANGE:
				handleLocationChange(i);
				break;
			case EVENT_OUTGOING_CALL:
				handleOutgoingCall(i);
				break;
			case EVENT_INCOMING_CALL:
				handleIncomingCall(i);
				break;
			case EVENT_OUTGOING_SMS:
				handleOutgoingSMS(i);
				break;
			case EVENT_INCOMING_SMS:
				handleIncomingSMS(i);
			}
		}

		private void handleOutgoingCall(Intent intent) {
			Log.i("DrunkDroid", "Outgoing Call");
			OutgoingCallEvent event = new OutgoingCallEvent(DrunkenService
					.getInstance().getLastKnownLocation(), intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
			DrunkenService.getInstance().getRepository().addEvent(event);
		}

		private void handleMoodReading(Intent i) {
			if (i.getExtras() != null) {
				Log.i("DrunkDroid", "Incoming MoodReading");
				Location location = DrunkenService.getInstance()
						.getLastKnownLocation();
				MoodEvent readingEvent = new MoodEvent(location, i.getShortExtra("mood", (short)0));
				DrunkenService.getInstance().getRepository().addEvent(
						readingEvent);
			} else {
				throw new IllegalArgumentException(
						"Intent contains no or invalid data!");
			}
		}

		private void handleIncomingSMS(Intent intent) {
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				Log.i("DrunkDroid", "Incoming SMS");
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

		private void handleIncomingCall(Intent i) {
			Log.i("DrunkDroid", "Incoming Call");
			IncomingCallEvent event = new IncomingCallEvent(DrunkenService
					.getInstance().getLastKnownLocation(), i.getStringExtra("phoneNumber"));
			DrunkenService.getInstance().getRepository().addEvent(event);
		}

		private void handleOutgoingSMS(Intent i) {
			Uri smsUri = Uri.parse("content://sms");
			String orderBy = "date desc";
			Cursor cur = DrunkenService.getInstance().getContentResolver()
					.query(smsUri, null, null, null, orderBy);
			if (cur.moveToNext()) {
				if (cur.getString(cur.getColumnIndex("type")).equals("2")) {
					Log.i("DrunkDroid", "Outgoing SMS");
					String phoneNumber = cur.getString(cur
							.getColumnIndex("address"));
					String message = cur.getString(cur.getColumnIndex("body"));
					OutgoingSMSEvent event = new OutgoingSMSEvent(
							DrunkenService.getInstance().getLastKnownLocation(),
							phoneNumber, message);
					DrunkenService.getInstance().getRepository()
							.addEvent(event);
				}
			}
		}

		private void handleLocationChange(Intent i) {
			Log.i("DrunkDroid", "Location change");
			Location location = (Location) (i.getExtras().get("location"));
			LocationEvent locationEvent = new LocationEvent(location);
			// Save event to trip and check for possible events with empty
			// locations.
			DrunkenService.getInstance().getRepository()
					.updateEventsWithoutLocation(location);
			DrunkenService.getInstance().getRepository()
					.addEvent(locationEvent);
		}
	}

}