package org.lineageos.lemonades.defaultsettingshack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "LemonadesDefaultSettingsHack";

    private void putSecureSettingsDefaultIntValue(Context context, String name, int value) {
        try {
            Log.i(TAG, "Attempting to query " + name + ".");

            int value = Settings.Secure.getInt(context.getContentResolver(), name);
            Log.i(TAG, "Successfully queried " + name + "=" + value + ", no need to set.");
        }
        catch (Settings.SettingNotFoundException e) {
            Log.i(TAG, "Settings value " + name + " is unset. Setting it to " + value + ".");
            if(Settings.Secure.putInt(context.getContentResolver(), name, value)) {
                Log.i(TAG, "Set success.");
            } else {
                Log.i(TAG, "Set failure.");
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (
            Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())
            // Note: ACTION_LOCKED_BOOT_COMPLETED will trigger once on direct boot regardless of presence of secure lockscreen
        ) {
            Log.i(TAG, "Fix start");
            putSecureSettingsDefaultIntValue(Settings.Secure.GMS_ENABLED, 1);
            Log.i(TAG, "Fix completed");
        }
    }
}
