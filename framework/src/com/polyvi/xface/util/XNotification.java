
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

package com.polyvi.xface.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.Toast;

import com.polyvi.xface.core.XISystemContext;

/**
 * 本类提供notifcation的实现方法供所有类调用.
 */
public class XNotification {
    private ProgressDialog mSpinnerDialog = null;
    private ProgressDialog mProgressDialog = null;
    private XISystemContext mSystemContext = null;

    public XNotification(XISystemContext systemContext){
        mSystemContext = systemContext;
    }

    /**
     * 显示alert提示框.
     *
     * @param message       提示信息
     * @param title         提示框标题
     * @param buttonLabel   按钮显示信息
     * @param jsCallback    XJsCallback对象
     */
    public synchronized void alert(final String message, final String title,
            final String buttonLabel,
            final AlertDialog.OnClickListener actionListener) {
        Runnable runnable = new Runnable() {
            public void run() {
                 AlertDialog dlg = createAlertDialog(message, title,
                         buttonLabel, actionListener);
                 dlg.show();
            };
        };
        mSystemContext.runOnUiThread(runnable);
    }

    /**
     * 根据设置的时间来显示alert提示框
     *
     * @param message
     *            提示信息
     * @param title
     *            提示框标题
     * @param buttonLabel
     *            按钮显示信息
     * @param jsCallback
     *            XJsCallback对象
     * @param duration
     *            alter框显示的时间
     */
    public synchronized void alert(final String message, final String title,
            final String buttonLabel,
            final AlertDialog.OnClickListener actionListener,
            final long duration) {
        Runnable runnable = new Runnable() {
            public void run() {
                final AutoCloseDialog autoCloseDialog = new AutoCloseDialog(
                        message, title,buttonLabel, actionListener);
                autoCloseDialog.show(duration);
            };
        };
        mSystemContext.runOnUiThread(runnable);
    }

    /**
     * 创建一个dialogAlert
     *
     * @param message
     *            alert框显示的消息
     * @param title
     *            alert框的标题
     * @param buttonLabel
     *            按钮的信息
     * @param actionListener
     * @return
     */
    private AlertDialog createAlertDialog(final String message,
            final String title, final String buttonLabel,
            final AlertDialog.OnClickListener actionListener) {
        AlertDialog.Builder dlg = new AlertDialog.Builder(mSystemContext.getContext());
        dlg.setMessage(message);
        dlg.setTitle(title);
        dlg.setCancelable(false);
        dlg.setPositiveButton(buttonLabel, actionListener);
        return dlg.create();
    }

    /**
     * 显示confirm提示框
     *
     * @param message       提示信息
     * @param title         提示框标题
     * @param buttonLabels  按钮显示信息(以','分割, 1-3个按钮)
     * @param jsCallback    XJsCallback对象
     */
    public synchronized void confirm(final String message, final String title,
            final String buttonLabels,
            final AlertDialog.OnClickListener positiveButtonListener,
            final AlertDialog.OnClickListener neutralButtonListener,
            final AlertDialog.OnClickListener negativeButtonListener) {
        final String[] fButtons = buttonLabels.split(",");
        Runnable runnable = new Runnable() {
            public void run() {

                AlertDialog.Builder dlg = new AlertDialog.Builder(mSystemContext.getContext());
                dlg.setMessage(message);
                dlg.setTitle(title);
                dlg.setCancelable(false);
                int count = fButtons.length;
                // 根据按钮数量设置PositiveButton、NeutralButton和NegativeButton
                if (count > 0) {
                    dlg.setPositiveButton(fButtons[0], positiveButtonListener);
                }
                if (count > 1) {
                    dlg.setNeutralButton(fButtons[1], neutralButtonListener);
                }
                if (count > 2) {
                    dlg.setNegativeButton(fButtons[2], negativeButtonListener);
                }
                dlg.create();
                dlg.show();
            };
        };
        mSystemContext.runOnUiThread(runnable);
    }

    /**
     * 设备播放默认蜂鸣声.
     *
     * @param count
     *            播放 notification 的次数
     */
    public void beep(final long count) {
        Uri ringtoneUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(mSystemContext.getContext(), ringtoneUri);

        // 如果设备设置成静音状态，则返回
        if (ringtone == null) {
            return;
        }

        for (long i = 0; i < count; ++i) {
            ringtone.play();
            // timeout是一个经验值，有可能beep的声音超时5s，则需要调用stop来停止播放
            long timeout = 5000;
            while (ringtone.isPlaying() && (timeout > 0)) {
                timeout -= 100;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            /**
             * 在Android2.1上调用ringtone.play后会一直循环播放，所以在超时过后需要stop
             * 同时，在Android2.2及其以上版本，有可能notification beep的播放时间超过5s，
             * 也需要调用stop接口来结束播放
             */
            ringtone.stop();
        }
    }

    /**
     * 根据指定的毫秒数震动设备.
     *
     * @param mills
     *            震动的毫秒数.
     */
    public void vibrate(final long mills) {
        long time = mills;
        if (mills == 0) {
            time = 500;        // 如果传入的毫秒数为0，则默认为半秒.
        }
        Vibrator vibrator = (Vibrator) mSystemContext.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

    /**
     * 显示spinner对话框
     * @param title   对话框的标题
     * @param message 对话框显示的消息
     */
    public synchronized void activityStart(final String title,
            final String message) {
        // 如果spinner已经显示在页面上先关闭之
        if (null != mSpinnerDialog) {
            mSpinnerDialog.dismiss();
            mSpinnerDialog = null;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                mSpinnerDialog = ProgressDialog.show(mSystemContext.getContext(), title, message,
                        true, true, new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                mSpinnerDialog = null;
                            }
                        });
            }
        };
        mSystemContext.runOnUiThread(runnable);
    }

    /**
     * 关闭 spinner.
     */
    public synchronized void activityStop() {
        if (mSpinnerDialog != null) {
            mSpinnerDialog.dismiss();
            mSpinnerDialog = null;
        }
    }

    /**
     * 显示进度条对话框.
     *
     * @param title     对话框的标题
     * @param message   对话框的消息
     */
    public synchronized void progressStart(final String title, final String message) {
        //如果进度条显示在页面上先关闭
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                mProgressDialog = new ProgressDialog(mSystemContext.getContext());
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setTitle(title);
                mProgressDialog.setMessage(message);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(0);
                mProgressDialog.setOnCancelListener(
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                mProgressDialog = null;
                            }
                        });
                mProgressDialog.show();
            }
        };
        mSystemContext.runOnUiThread(runnable);
    }

    /**
     * 设置进度条的当前值.
     *
     * @param value     0-100
     */
    public synchronized void progressValue(int value) {
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(value);
        }
    }

    /**
     * 停止进度条的显示.
     */
    public synchronized void progressStop() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 显示一个toast窗口,以长时间显示，用户不用指定显示时间.
     *
     * @param message
     *            要显示的信息
     */
    public void toast(final String message) {
        toast(message, Toast.LENGTH_LONG);
    }

    /**
     * 显示一个toast窗口,用户需要指定显示时间
     *
     * @param message
     *            要显示的信息
     * @param duration
     *            显示toast的时间(秒)<br>
     */
    public void toast(final String message, final int duration) {
        Runnable runnable = new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(mSystemContext.getContext(), message,
                        duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        };
        mSystemContext.runOnUiThread(runnable);
    }

    class AutoCloseDialog {

        private AlertDialog mDialog;
        private ScheduledExecutorService mExecutor = Executors
                .newSingleThreadScheduledExecutor();

        public AutoCloseDialog(final String message,
                final String title, final String buttonLabel,
                final AlertDialog.OnClickListener actionListener) {
            this.mDialog = createAlertDialog(message, title, buttonLabel, actionListener);
        }


        public void show(long duration) {
            // 创建自动关闭任务
            Runnable runner = new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            };
            // 新建调度任务
            mExecutor.schedule(runner, duration, TimeUnit.MILLISECONDS);
            mDialog.show();
        }
    }
}
