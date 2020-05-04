package com.h4rz.socialdistancing.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.h4rz.socialdistancing.utility.Constants
import com.h4rz.socialdistancing.utility.SnoozeUtils


/**
 * Created by Darshan Parikh on 04/05/20.
 */

class SnoozeTimeOverReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null) return
        if(intent == null) return
        if(intent.action != Constants.ACTION_SNOOZE_TIME_OVER) return
        Log.i(TAG, "Snooze time over")

        SnoozeUtils.setShowNotification(context, true)
    }

    companion object {
        val TAG: String = SnoozeTimeOverReceiver::class.java.simpleName
    }
}