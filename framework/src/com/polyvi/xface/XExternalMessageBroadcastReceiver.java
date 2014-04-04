package com.polyvi.xface;

import com.polyvi.xface.event.XEvent;
import com.polyvi.xface.event.XEventType;
import com.polyvi.xface.event.XSystemEventCenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收库模式传递过来参数消息的广播
 */
public class XExternalMessageBroadcastReceiver extends BroadcastReceiver {

	private static final String RECEAIVER_ACTION = "com.android.broadcastAction.EXTERNALMESSAGE";
	private static final String RECEIVE_EXTERNAL_MESSAGE = "external_message";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (RECEAIVER_ACTION.equals(intent.getAction())) {
			String message = intent.getStringExtra(RECEIVE_EXTERNAL_MESSAGE);
			XEvent evt = XEvent.createEvent(
					XEventType.EXTERNAL_MESSAGE_RECEIVED, message);
			((XFaceMainActivity)context).getEventCenter().sendEventSync(evt);
		}
	}

}
