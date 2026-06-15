package org.lineageos.lemonades.gmsenablerhack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "LemonadesGMSEnablerHack";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (
            Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())  // will trigger regardless of presence of lock screen
            // Note: ACTION_LOCKED_BOOT_COMPLETED will trigger once on direct boot regardless of presence of secure lockscreen
        ) {
            Log.i(TAG, "Fix start");
            try {
                Log.i(TAG, "Attempting to query GMS enablement setting.");

                int gms_enabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.GMS_ENABLED);
                Log.i(TAG, "Successfully queried gms_enabled=" + gms_enabled + ", no need to perform fix.");
            }
            catch (Settings.SettingNotFoundException e) {
                Log.i(TAG, "GMS enablement settings is unset. Setting it to 1.");
                if(Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.GMS_ENABLED, 1)) {
                    Log.i(TAG, "Set success.");
                } else {
                    Log.i(TAG, "Set failure.");
                }
            }
            Log.i(TAG, "Fix completed");
        }
    }
}
