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

    private void putSecureSettingsDefaultValue(Context context, String name, String value) {
        Log.i(TAG, "Attempting to query " + name + ".");

        String existingValue = Settings.Secure.getString(context.getContentResolver(), name);
        if(null != existingValue) {
            Log.i(TAG, "Successfully queried " + name + "=" + value + ", no need to set.");
            return;
        }
        Log.i(TAG, "Settings value " + name + " is unset. Setting it to " + value + ".");
        if(Settings.Secure.putString(context.getContentResolver(), name, value)) {
            Log.i(TAG, "Set success.");
        } else {
            Log.i(TAG, "Set failure.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (
            Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(intent.getAction())
            // Note: ACTION_LOCKED_BOOT_COMPLETED will trigger once on direct boot regardless of presence of secure lockscreen
        ) {
            Log.i(TAG, "Fix start");
            
            // FIXME: https://github.com/AviumUI/android_manifests/issues/6
            // GMS is disabled by AviumSettings by default, making it impossible to complete SetupWizard if GMS
            // is prebuilt or installed with addon packages.
            putSecureSettingsDefaultValue(context, Settings.Secure.GMS_ENABLED, "1");

            // FIXME: https://github.com/AviumUI/android_manifests/issues/7
            // "Show media squiggle animation" is by default displayed as enabled in AviumSettings,
            // but actually disabled by default.
            putSecureSettingsDefaultValue(context, Settings.Secure.SHOW_MEDIA_SQUIGGLE_ANIMATION, "0");

            // FIXME: Ultra Dim default strength inconsistency
            // (zero on UI but obviously not zero actually)
            putSecureSettingsDefaultValue(context, Settings.Secure.REDUCE_BRIGHT_COLORS_LEVEL, "0");

            // HDR strength
            // (set a sane default here since full strength will cause switching flickering due to quirks)
            putSecureSettingsDefaultValue(context, Settings.Secure.HDR_BRIGHTNESS_BOOST_LEVEL, "0.65");
            
            Log.i(TAG, "Fix completed");
        }
    }
}
