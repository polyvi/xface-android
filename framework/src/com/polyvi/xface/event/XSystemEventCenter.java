
/*
 Copyright 2012-2013, Polyvi Inc. (http://polyvi.github.io/openxface)
 This program is distributed under the terms of the GNU General Public License.

 This file is part of xFace.

 xFace is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 xFace is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with xFace.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.polyvi.xface.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.polyvi.xface.util.XLog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 系统事件中心 处理系统事件的分发以及注册事件接收器
 * 
 */
public class XSystemEventCenter {
    private static final String CLASS_NAME = XSystemEventCenter.class.getName();

    static final int MSG_EXEC_PENDING_EVENT = 1;
    private final Context mContext;
    private static final Object mLock = new Object();
    private final Handler mHandler;
    private static XSystemEventCenter mInstance;
    private Map<Integer, ArrayList<XISystemEventReceiver>> mReceivers;
    private List<EventReceiverRecord> mEvtReceiverRecords;

    public static XSystemEventCenter getInstance() {
        if(null == mInstance) {
            XLog.e(CLASS_NAME, "System Event Center not init!");
        }
        return mInstance;
    }

    /**
     * 初始化XSystemEventCenter
     * @param context
     */
    public static void init(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new XSystemEventCenter(context);
            }
        }
    }

    private XSystemEventCenter(Context context) {
        mContext = context;
        mEvtReceiverRecords = new ArrayList<XSystemEventCenter.EventReceiverRecord>();
        mReceivers = new ConcurrentHashMap<Integer, ArrayList<XISystemEventReceiver>>();
        mHandler = new Handler(context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MSG_EXEC_PENDING_EVENT:
                    notifyPendingEvent();
                    break;
                default:
                    super.handleMessage(msg);
                }
            }

        };
    }

    /**
     * 表示等待执行的事件记录
     * 
     */
    private static class EventReceiverRecord {
        public XEvent evt;
        public ArrayList<XISystemEventReceiver> receivers;

        public EventReceiverRecord(XEvent event,
                ArrayList<XISystemEventReceiver> recevierList) {
            evt = event;
            receivers = recevierList;
        }
    }

    /**
     * 注册事件接收器
     * 
     * @param receiver
     * @param evtType
     */
    public void registerReceiver(XISystemEventReceiver receiver,
            int evtType) {
        synchronized (mReceivers) {
            ArrayList<XISystemEventReceiver> receivers = mReceivers
                    .get(evtType);
            if (null == receivers) {
                receivers = new ArrayList<XISystemEventReceiver>();
                mReceivers.put(evtType, receivers);
            }
            receivers.add(receiver);
        }

    }

    /**
     * 反注册事件接收器
     * 
     * @param receiver
     */
    public void unregisterReceiver(XISystemEventReceiver receiver) {
        synchronized (mReceivers) {
            Iterator<Entry<Integer, ArrayList<XISystemEventReceiver>>> iter = mReceivers
                    .entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, ArrayList<XISystemEventReceiver>> entry = iter
                        .next();
                ArrayList<XISystemEventReceiver> receivers = entry.getValue();
                if (null != receivers) {
                    for (int k = 0; k < receivers.size(); k++) {
                        if (receivers.get(k) == receiver) {
                            receivers.remove(k);
                            k--;
                        }
                    }
                    int evtType = entry.getKey();
                    if (receivers.size() <= 0) {
                        mReceivers.remove(evtType);
                    }
                }
            }
        }

    }

    /**
     * 发送事件(同步)
     * 
     * @param evt
     */
    public void sendEventSync(XEvent evt) {
        ArrayList<XISystemEventReceiver> pendingReceivers = null;
        synchronized (mReceivers) {
            pendingReceivers = findReceivers(evt);
        }
        if(null == pendingReceivers) {
            return;
        }
        if (pendingReceivers.size() > 0) {
            notifyReceivers(pendingReceivers, evt);
        }
    }

    /**
     * 发送事件(异步)
     * 
     * @param evt
     * @return
     */
    public boolean sendEventAsync(XEvent evt) {
        synchronized (mReceivers) {
            ArrayList<XISystemEventReceiver> pendingReceivers = findReceivers(evt);

            if (pendingReceivers.size() > 0) {
                EventReceiverRecord record = new EventReceiverRecord(evt,
                        pendingReceivers);
                mEvtReceiverRecords.add(record);
                if (!mHandler.hasMessages(MSG_EXEC_PENDING_EVENT)) {
                    mHandler.sendEmptyMessage(MSG_EXEC_PENDING_EVENT);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 查找对应的事件接收器
     * @param sendEvt 发送的事件
     * @return
     */
    private ArrayList<XISystemEventReceiver> findReceivers(
            XEvent evt) {
        int sendEvt = evt.getType();
        Iterator<Entry<Integer, ArrayList<XISystemEventReceiver>>> iter = mReceivers
                .entrySet().iterator();
        ArrayList<XISystemEventReceiver> pendingReceivers = new ArrayList<XISystemEventReceiver>();
        while (iter.hasNext()) {
            Entry<Integer, ArrayList<XISystemEventReceiver>> entry = iter
                    .next();
            int evtType = entry.getKey();
            if (sendEvt == evtType) {
                ArrayList<XISystemEventReceiver> receivers = entry
                        .getValue();
                if (null != receivers) {
                    for (int k = 0; k < receivers.size(); k++) {
                        XISystemEventReceiver receiver = receivers.get(k);
                        pendingReceivers.add(receiver);
                    }
                }
            }
        }
        return pendingReceivers;
    }

    /**
     * 通知事件接收器
     */
    private void notifyPendingEvent() {
        while (true) {
            EventReceiverRecord[] per = null;
            synchronized (mReceivers) {
                int size = mEvtReceiverRecords.size();
                if (size <= 0) {
                    return;
                }
                per = new EventReceiverRecord[size];
                mEvtReceiverRecords.toArray(per);
                mEvtReceiverRecords.clear();
            }

            for (int i = 0; i < per.length; i++) {
                EventReceiverRecord er = per[i];
                notifyReceivers(er.receivers, er.evt);
            }
        }
    }

    /**
     * 通知每个事件接收器
     * @param receivers
     * @param event
     */
    private void notifyReceivers(
            ArrayList<XISystemEventReceiver> receivers,
            XEvent event) {
        for (int index = 0; index < receivers.size(); index++) {
            receivers.get(index).onReceived(mContext, event);
        }
    }
}
