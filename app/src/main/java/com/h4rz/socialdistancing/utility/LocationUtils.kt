/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.h4rz.socialdistancing.utility

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import com.h4rz.socialdistancing.R
import com.h4rz.socialdistancing.utility.Constants.ADDRESS
import com.h4rz.socialdistancing.utility.Constants.KEY_REQUESTING_LOCATION_UPDATES
import com.h4rz.socialdistancing.utility.Constants.LATITUDE
import com.h4rz.socialdistancing.utility.Constants.LONGITUDE
import java.text.DateFormat
import java.util.*

internal object LocationUtils {


    /**
     * Returns true if requesting location updates, otherwise returns false.
     *
     * @param context The [Context].
     */
    fun requestingLocationUpdates(context: Context?): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false)
    }

    /**
     * Stores the location updates state in SharedPreferences.
     * @param requestingLocationUpdates The location updates state.
     */
    fun setRequestingLocationUpdates(
        context: Context?,
        requestingLocationUpdates: Boolean
    ) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates)
            .apply()
    }

    /**
     * Returns the `location` object as a human readable string.
     * @param location  The [Location].
     */
    fun getLocationText(location: Location?): String {
        return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
    }

    fun getLocationTitle(context: Context): String {
        return context.getString(
            R.string.location_updated,
            DateFormat.getDateTimeInstance().format(Date())
        )
    }

    fun saveLastLocationLatitude(context: Context?, location: Location) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putFloat(LATITUDE, location.latitude.toFloat())
            .apply()
    }

    fun getLastLocationLatitude(context: Context?): Float {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(LATITUDE, 0f)
    }

    fun saveLastLocationLongitude(context: Context?, location: Location) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putFloat(LONGITUDE, location.longitude.toFloat())
            .apply()
    }

    fun getLastLocationLongitude(context: Context?): Float {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(LONGITUDE, 0f)
    }

    fun saveLastAddressDetails(context: Context?, address: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(ADDRESS, address)
            .apply()
    }

    fun getLastAddressDetails(context: Context?): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ADDRESS, "") ?: ""
    }

}