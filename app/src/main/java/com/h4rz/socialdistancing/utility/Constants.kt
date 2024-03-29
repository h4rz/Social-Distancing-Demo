package com.h4rz.socialdistancing.utility

import org.altbeacon.beacon.Identifier

/**
 * Created by Harsh Rajgor on 27/04/20.
 */
object Constants {
    const val ALT_BEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"
    const val COVID_EXPOSURE_LAYOUT = "s:0-1=fd6f,p:-:-59,i:2-17,d:18-21"
    const val BEACON_LAYOUT = COVID_EXPOSURE_LAYOUT
    const val NOTIFICATION_INTERVAL_IN_MS = 15000L
    const val FOREGROUND_NOTIFICATION_ID = 456
    const val WARNING_NOTIFICATION_ID = 999
    const val SAFE_DISTANCE_IN_METERS = 3.0
    const val BLUETOOTH_INTENT_REQUEST_CODE = 123

    const val ACTION_NOTIFICATION_SNOOZE = "notification_snooze"
    const val ACTION_SNOOZE_TIME_OVER = "snooze_time_over"
    const val NOTIFICATION_SNOOZE_TIME = "notification_snooze_time"

    // Time in millis
    const val ZERO_MILLISECOND: Long = 0
    const val ONE_MILLISECOND: Long = 1
    const val ONE_SECOND: Long = 1000 * ONE_MILLISECOND
    const val ONE_MINUTE: Long = 60 * ONE_SECOND
    const val ONE_HOUR: Long = 60 * ONE_MINUTE

    /**
     * An AltBeacon is identified by a unique three part identifier.
     * The first identifier Id1 is normally used across an organization,
     * the second identifier Id2 is used to group beacons
     * and the third identifier Id3 is used to uniquely identify a specific beacon (in combination with the other two identifiers.)
     */

    // Converting String (com.h4rz.socialdistancing) -> Hexadecimal ("636f6d2e-6834-727a-2e73-6f6369616c64")
    val CUSTOM_IDENTIFIER: Identifier =
        Identifier.parse(getHexadecimalIdentifierString("com.h4rz.socialdistancing"))
    //val CUSTOM_IDENTIFIER: Identifier = Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6") // Default ID of altbeacon

    const val PACKAGE_NAME =
        "com.google.android.gms.location.sample.locationupdatesforegroundservice"

    /**
     * The name of the channel for notifications.
     */
    const val CHANNEL_ID = "channel_01"
    const val ACTION_BROADCAST =
        "$PACKAGE_NAME.broadcast"
    const val EXTRA_LOCATION =
        "$PACKAGE_NAME.location"
    const val EXTRA_STARTED_FROM_NOTIFICATION =
        PACKAGE_NAME +
                ".started_from_notification"

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2

    const val NOTIFICATION_ID = 12345678

    const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"
    const val KEY_IS_SHOW_NOTIFICATION = "is_show_notification"

    const val LATITUDE = "latitude"

    const val LONGITUDE = "longitude"

    const val ADDRESS = "address"
}