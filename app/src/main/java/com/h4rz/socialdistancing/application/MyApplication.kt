package com.h4rz.socialdistancing.application

import android.app.Application
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.h4rz.socialdistancing.activities.MainActivity
import com.h4rz.socialdistancing.notification.NotificationManager
import com.h4rz.socialdistancing.utility.Constants.ALT_BEACON_LAYOUT
import com.h4rz.socialdistancing.utility.Constants.CUSTOM_IDENTIFIER
import com.h4rz.socialdistancing.utility.Constants.FOREGROUND_NOTIFICATION_ID
import com.h4rz.socialdistancing.utility.Constants.NOTIFICATION_INTERVAL_IN_MS
import com.h4rz.socialdistancing.utility.Constants.SAFE_DISTANCE_IN_METERS
import org.altbeacon.beacon.*
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import java.util.*
import kotlin.math.abs


/**
 * Created by Harsh Rajgor on 22/04/20.
 */

class MyApplication : Application(), BootstrapNotifier, RangeNotifier {

    private val tag = MainActivity::class.java.simpleName
    private val deviceID = UUID.randomUUID().toString()
    private var regionBootstrap: RegionBootstrap? = null
    private lateinit var backgroundPowerSaver: BackgroundPowerSaver
    private lateinit var beaconManager: BeaconManager
    private lateinit var region: Region
    private var mainActivity: MainActivity? = null
    private lateinit var context: Context
    private var isNotificationSent = false

    override fun onCreate() {
        super.onCreate()
        initializeVariables()
        enableForegroundScanningService()
        beaconTransmissionInBackground()
        resetTimer()
    }

    private fun initializeVariables() {
        context = this
        isNotificationSent = false
    }

    private fun resetTimer() {
        Handler().postDelayed({
            if (isNotificationSent)
                NotificationManager().removeWarningNotifications(context)
            isNotificationSent = false
            resetTimer()
        }, NOTIFICATION_INTERVAL_IN_MS)
    }

    private fun enableForegroundScanningService() {
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(ALT_BEACON_LAYOUT))
        val title = "Scanning..."
        val body = "Scanning for persons nearby."
        val builder = NotificationManager().getNotificationBuilder(this, title, body)
        beaconManager.enableForegroundServiceScanning(builder.build(), FOREGROUND_NOTIFICATION_ID)

        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100
        region = Region(deviceID, null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
        backgroundPowerSaver = BackgroundPowerSaver(this)
    }

    private fun beaconTransmissionInBackground() {
        val beacon = Beacon.Builder()
            .setId1(CUSTOM_IDENTIFIER.toString())
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
            .setTxPower(-59)
            .setDataFields(listOf(0L)) // Remove this for beacon layouts without d: fields
            .build()
        // Change the layout below for other beacon types
        val beaconParser = BeaconParser()
            .setBeaconLayout(ALT_BEACON_LAYOUT)
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(tag, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(tag, "Advertisement start succeeded.")
            }
        })
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
        Log.i(
            tag,
            "Current region state is: " + if (state == 1) "INSIDE" else "OUTSIDE ($state)"
        )
    }

    override fun didEnterRegion(region: Region?) {
        Log.i(tag, "Did enter region.")
        isNotificationSent = false
        try {
            beaconManager.addRangeNotifier(this)
            if (region != null)
                beaconManager.startRangingBeaconsInRegion(region)
        } catch (e: Exception) {
            Log.i(tag, "Exception While Ranging " + e.message)
        }
    }

    override fun didExitRegion(region: Region?) {
        Log.i(tag, "I no longer see a beacon")
        isNotificationSent = false
    }

    /*fun disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap!!.disable()
            regionBootstrap = null
        }
    }*/

    fun enableMonitoring() {
        region = Region(deviceID, null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
    }

    fun setMainActivity(activity: MainActivity?) {
        this.mainActivity = activity
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        if (beacons != null) {
            if (beacons.isNotEmpty()) {
                for (beacon in beacons) {
                    if (beacon.id1 == CUSTOM_IDENTIFIER) {
                        val distance = abs(beacon.distance)
                        if (distance < SAFE_DISTANCE_IN_METERS) {
                            val title = "Warning"
                            val message =
                                "I see a person that is ${String.format(
                                    "%.2f",
                                    beacon.distance
                                )} metres away. Please maintain $SAFE_DISTANCE_IN_METERS meters distance."
                            if (!isNotificationSent) {
                                NotificationManager().buildNotification(context, title, message)
                                isNotificationSent = true
                            }
                            //Toast.makeText(context, message,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
