package com.wsdc.a.accessibility;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.wsdc.a.accessibility.MyAccessibilityService.Constants;

public class ToastOrNotificationTestActivity extends Activity {
    private TextView text;
    private static final String TAG = "ToastOrNotification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_or_notification_test);
        text= (TextView) findViewById(R.id.text);
        Log.d(TAG,"Entering");
        final IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_CATCH_NOTIFICATION);
        mIntentFilter.addAction(Constants.ACTION_CATCH_TOAST);
        registerReceiver(toastOrNotificationCatcherReceiver, mIntentFilter);

        Log.v(TAG, "Receiver registered.");
        if(isMyServiceRunning(MyAccessibilityService.class)){
            Log.d(TAG,"Service Running");
        }else{
            Log.d(TAG,"Not Running");
        }
        text.setText("Welcome");
        if(!MyAccessibilityService.isAccessibilitySettingsOn(ToastOrNotificationTestActivity.this)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ToastOrNotificationTestActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle("APP PERMISSIONS");
            dialog.setMessage("Please switch on the accessibility settings for the app to run" );
            dialog.setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(ToastOrNotificationTestActivity.this, , Toast.LENGTH_SHORT).show();
                            text.setText("App Won't work without accessibility permissions. Please Turn on Accessibility for the App");
                        }
                    });

            final AlertDialog alert = dialog.create();
            alert.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(toastOrNotificationCatcherReceiver);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        Log.d(TAG,"IN service Running");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private final BroadcastReceiver toastOrNotificationCatcherReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Received message");
            Log.v(TAG, "intent.getAction() :: " + intent.getAction());
            Log.v(TAG, "intent.getStringExtra(Constants.EXTRA_PACKAGE) :: " + intent.getStringExtra(Constants.EXTRA_PACKAGE));
            Log.v(TAG, "intent.getStringExtra(Constants.EXTRA_MESSAGE) :: " + intent.getStringExtra(Constants.EXTRA_MESSAGE));
//            text.setText("text"+intent.getStringExtra(Constants.EXTRA_MESSAGE));
        }
    };


}

