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
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class EventHandler extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, final Intent intent) {
		Thread t = new Thread() {
			@Override
			public void run() {
				if (intent.getAction().equals("NEW_MOOD_READING")) {
					HandleMoodReading(intent);
					Log.i("DrunkDroid", "Incoming MoodReading");
				}
				if (intent.getAction().equals(
						"android.provider.Telephony.SMS_RECEIVED")) {
					HandleIncomingSMS(intent);
					Log.i("DrunkDroid", "Incoming SMS");
				}
				if (intent.getAction().equals(
						"android.intent.action.NEW_OUTGOING_CALL")) {
					handleOutgoingCall(intent);
					Log.i("DrunkDroid", "Outgoing Call");
				}
			}
		};
		t.start();
	}

	public void handleIncomingCall(final String phoneNumber) {
		Log.i("DrunkDroid", "Incoming Call");
		Thread t = new Thread() {
			@Override
			public void run() {
				// TODO Get name from phonebook and call duration
				IncomingCallEvent event = new IncomingCallEvent(DrunkenService
						.getInstance().getLastKnownLocation(), "NA",
						phoneNumber, (long) 0);
				DrunkenService.getInstance().getRepository().addEvent(event);
			}
		};
		t.start();
	}

	private void handleOutgoingCall(Intent intent) {
		OutgoingCallEvent event = new OutgoingCallEvent(DrunkenService
				.getInstance().getLastKnownLocation(), "NA", intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER), (long) 0);
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
			DrunkenService.getInstance().getRepository().addEvent(readingEvent);
			System.out.println("Sending MoodReading : "
					+ location.getLatitude() + " x " + location.getLongitude());
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
			for(SmsMessage m : messages) {
				message += m.getMessageBody();
			}
		
			IncomingSMSEvent event = new IncomingSMSEvent(DrunkenService.getInstance().getLastKnownLocation(), "NA", messages[0].getOriginatingAddress(), message);
			DrunkenService.getInstance().getRepository().addEvent(event);
		}
	}

	public void handleOutgoingSMS(final Context context) {
		Thread t = new Thread() {
			@Override
			public void run() {
				Uri smsUri = Uri.parse("content://sms");
				String orderBy = "date desc";
				Cursor cur = context.getContentResolver().query(smsUri, null, null, null,
						orderBy);
				if (cur.moveToNext()) {				
					if (cur.getString(cur.getColumnIndex("type")).equals("2")) {
						Log.i("DrunkDroid", "Outgoing SMS");
						String phoneNumber = cur.getString(cur.getColumnIndex("address"));
						String message = cur.getString(cur.getColumnIndex("body"));
						OutgoingSMSEvent event = new OutgoingSMSEvent(DrunkenService.getInstance().getLastKnownLocation(), "NA", phoneNumber, message);
						DrunkenService.getInstance().getRepository().addEvent(event);
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
				// Save event to trip and check for possible events with empty locations.
				DrunkenService.getInstance().getRepository()
						.updateEventsWithoutLocation(location);
				DrunkenService.getInstance().getRepository().addEvent(
						locationEvent);
			}
		};
		t.start();
	}
}