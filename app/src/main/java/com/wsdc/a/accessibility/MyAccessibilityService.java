package com.wsdc.a.accessibility;


import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MyAccessibilityService extends AccessibilityService {

    private final AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG,"service connected");
        final int eventType = event.getEventType();
        final AccessibilityNodeInfo source = event.getSource();
        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            final String sourcePackageName = (String)event.getPackageName();
            Parcelable parcelable = event.getParcelableData();

            if (parcelable instanceof Notification) {

                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                    final String notificationMsg = ""+messages.get(0);
                    Log.v(TAG, "Captured notification message [" + notificationMsg + "] for source [" + sourcePackageName + "]");
                    Log.v(TAG, "Broadcasting for " + Constants.ACTION_CATCH_NOTIFICATION);
                    try {
                        Intent mIntent = new Intent(Constants.ACTION_CATCH_NOTIFICATION);
                        mIntent.putExtra(Constants.EXTRA_PACKAGE, sourcePackageName);
                        mIntent.putExtra(Constants.EXTRA_MESSAGE, notificationMsg);
                        MyAccessibilityService.this.getApplicationContext().sendBroadcast(mIntent);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Log.e(TAG, "Notification Message is empty. Can not broadcast");
                }
            } else {
                // Something else, e.g. a Toast message
                // Read message and broadcast
                List<CharSequence> messages = event.getText();
                if (messages.size() > 0) {
                    final String toastMsg = ""+ messages.get(0);
                    Log.v(TAG, "Captured message [" + toastMsg + "] for source [" + sourcePackageName + "]");
                    Log.v(TAG, "Broadcasting for " + Constants.ACTION_CATCH_TOAST);
                    if(toastMsg.contains("Android")){
                        String replace=toastMsg.replaceAll("Android","Hacked");
                        Bundle arguments = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,replace);
                        source.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(), arguments);
                    }
                    try {
                        Intent mIntent = new Intent(Constants.ACTION_CATCH_TOAST);
                        mIntent.putExtra(Constants.EXTRA_PACKAGE, sourcePackageName);
                        mIntent.putExtra(Constants.EXTRA_MESSAGE, toastMsg);
                        MyAccessibilityService.this.getApplicationContext().sendBroadcast(mIntent);
                    } catch (Exception e) {
                        Log.v(TAG, e.toString());
                    }


                    if (source == null) {
                        return;
                    }
                } else {
                    Log.e(TAG, "Message is empty. Can not broadcast");
                }
            }
        } else {
            Log.v(TAG, "Got un-handled Event");
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {

        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        } else {
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        }

        info.notificationTimeout = 100;
        Log.d(TAG,"service connected");
        this.setServiceInfo(info);

    }

    public static final class Constants {

        public static final String EXTRA_MESSAGE = "extra_message";
        public static final String EXTRA_PACKAGE = "extra_package";
        public static final String ACTION_CATCH_TOAST = "com.wsdc.a.accessibility.CATCH_TOAST";
        public static final String ACTION_CATCH_NOTIFICATION = "com.wsdc.a.accessibility.CATCH_NOTIFICATION";
    }


    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.wsdc.a.accessibility/com.wsdc.a.accessibility.MyAccessibilityService";

        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }
}
