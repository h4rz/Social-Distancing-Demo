package com.h4rz.socialdistancing.utility

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by Darshan Parikh on 04/05/20.
 */

object SnoozeUtils {
    fun isShowNotification(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(Constants.KEY_IS_SHOW_NOTIFICATION, true)
    }

    fun setShowNotification(context: Context, isShow: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(Constants.KEY_IS_SHOW_NOTIFICATION, isShow)
            .apply()
    }
}