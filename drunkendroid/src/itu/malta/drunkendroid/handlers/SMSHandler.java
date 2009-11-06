package itu.malta.drunkendroid.handlers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.gsm.SmsMessage;

public class SMSHandler extends Handler {

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);

		// Message is incoming
		if (msg.arg1 == 1) {
			Intent intent = (Intent) msg.obj;
			getIncomingMessage(intent);
		}
		// Message is outgoing
		else {

		}
	}

	private void getIncomingMessage(Intent intent) {
		if (intent.getAction()
				.equals("android.intent.action.DATA_SMS_RECEIVED")) {

			StringBuilder sb = new StringBuilder();
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdusObj.length];

				for (int i = 0; i < pdusObj.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}

			}
		}
	}
}
