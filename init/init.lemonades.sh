#! /vendor/bin/sh

print_log() {
    log -t init.lemonades.sh "$*"
}

# ======== boot_completed stage below ========
print_log "Waiting for boot to complete."
until [[ $(getprop sys.boot_completed) == "1" ]]; do
    sleep 1
done

# FIXME: Display refresh rate temp fix.
# Fixes display refresh sometimes stuck at 60Hz after boot by quickly changing the setting to minimum and then back.
refresh_rate_fix() {
    print_log "refresh_rate_fix: Start"

    settings="/system/bin/cmd settings"

    # Repeat until settings service starts.
    until prev_peak_refresh_rate=$($settings get system peak_refresh_rate); do
        print_log "refresh_rate_fix: Failed to get settings. Aborting."
        print_log "refresh_rate_fix: $($settings get system peak_refresh_rate 2>&1)"
        return
    done
    print_log "refresh_rate_fix: Former peak_refresh_rate is $prev_peak_refresh_rate"
    prev_min_refresh_rate=$($settings get system min_refresh_rate)
    print_log "refresh_rate_fix: Former min_refresh_rate is $prev_min_refresh_rate"

    # Set refresh rate values to minimum
    print_log "refresh_rate_fix: Changing refresh rate to 60"
    if [[ "$prev_min_refresh_rate" != "null" ]]; then
        $settings put system min_refresh_rate 0.0
    fi
    if [[ "$prev_peak_refresh_rate" != "null" ]]; then
        $settings put system peak_refresh_rate 60.0
    fi

    # Wait for a while
    sleep 1

    # Set refresh rate values back
    print_log "refresh_rate_fix: Restoring refresh rate"
    if [[ "$prev_peak_refresh_rate" != "null" ]]; then
        $settings put system peak_refresh_rate "$prev_peak_refresh_rate"
    fi
    if [[ "$prev_min_refresh_rate" != "null" ]]; then
        $settings put system min_refresh_rate "$prev_min_refresh_rate"
    fi

    print_log "refresh_rate_fix: Done"
}
refresh_rate_fix
