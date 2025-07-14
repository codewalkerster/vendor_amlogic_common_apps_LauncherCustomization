package com.droidlogic.setup.provider;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityManager;
import android.app.ActivityTaskManager.RootTaskInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ComponentName;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import com.droidlogic.app.SystemControlManager;

import java.util.Set;
import android.text.TextUtils;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothAdapter;

/** Hook Begin. */
public class HookBeginActivity extends Activity {
    private static final String TAG = "HookBeginActivity";
    public static final String PROP_BT = "vendor.tv.initial.startbtpair";
    private static final String ACTION_BLUETOOTH_PAIRING =
        "com.google.android.tv.googletvremotepairer.action.BLUETOOTH_PAIRING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BtPairingControl control = new BtPairingControl();
        control.startBtPairIfNeed();

        setResult(-1);
        final Uri CONTENT_URI = Uri.parse("content://com.google.android.tungsten.setupwraith.locales/localeprefs");
        //ArrayList<String> preferredLocalesList = new ArrayList<>();
        // Perform any logic necessary to determine the list of preferred locales here. This could be things
        // such as reading the "ro.oem.key" system property, or determining user location.
        //preferredLocalesList.add("en-US");
        //preferredLocalesList.add("en-CA");
        //preferredLocalesList.add("es-US");
        //preferredLocalesList.add("fr-CA");
        String[] preferredLocalesList = getResources().getStringArray(R.array.welcome_preferred_locales);
        for (int rank = 0; rank < preferredLocalesList.length; rank++) {
            String localeTag = preferredLocalesList[rank];
            Log.d(TAG, "localeTag is :" + localeTag + ";");
            ContentValues values = new ContentValues();
            values.put("locale", localeTag);
            values.put("rank", rank + 1);

            getContentResolver().insert(CONTENT_URI, values);
        }

        // Hide the other supported locales.
        ContentValues values = new ContentValues();
        values.put("locale", "*");
        values.put("rank", 1);
        getContentResolver().insert(CONTENT_URI, values);
        Log.d(TAG, "content finish:"+getContentResolver().toString());
        finish();
    }

    private void startBtPair() {
        if (("true").equals(getSystemControlManager().getProperty(PROP_BT))) {
            return;
        }
        Intent intent = new Intent(ACTION_BLUETOOTH_PAIRING);
        startActivity(intent);
        Log.d(TAG,"startActivity");
    }
    public static SystemControlManager getSystemControlManager() {
         return SystemControlManager.getInstance();
    }

    private class BtPairingControl {
        public BtPairingControl() {
        }
        public void startBtPairIfNeed() {
            if (isBtPairNeeded()) {
                Log.w(TAG, "startBtPair");
                startBtPair();
            } else {
                Log.w(TAG, "No need to startBtPair");
            }
        }
        public boolean isBtPairNeeded() {

            BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBtAdapter == null) {
                Log.i(TAG, "Can't get BT adapter, return");
                return false;
            }

            final Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
            if (bondedDevices == null) {
                Log.w(TAG, "No bondedDevices!");
                return true;
            }

            for (final BluetoothDevice device : bondedDevices) {
                final String deviceAddress = device.getAddress();
                String deviceName = device.getName() != null ?  device.getName().replaceAll("[ *@#$%^&-]", "") : "null";
                Log.i(TAG, "device: "+ deviceName);
                if (TextUtils.isEmpty(deviceAddress)) {
                    Log.w(TAG, "Skipping mysteriously empty bluetooth device");
                    continue;
                }

                BluetoothClass btClass = device.getBluetoothClass();
                if (btClass != null &&
                          btClass.getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL)
                    return false;
            }
            return true;
        }

    }

}
