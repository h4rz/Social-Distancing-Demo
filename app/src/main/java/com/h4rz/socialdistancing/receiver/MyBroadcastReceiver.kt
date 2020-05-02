package com.h4rz.socialdistancing.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.widget.Toast
import com.h4rz.socialdistancing.utility.Constants.EXTRA_LOCATION
import com.h4rz.socialdistancing.utility.LocationUtils

/**
 * Created by Harsh Rajgor on 30/04/20.
 */

class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val location: Location = intent?.getParcelableExtra(EXTRA_LOCATION)!!
        Toast.makeText(context, LocationUtils.getLocationText(location), Toast.LENGTH_SHORT).show()
    }
}