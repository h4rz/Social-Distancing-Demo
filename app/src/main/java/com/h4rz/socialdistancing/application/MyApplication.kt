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
import org.altbeacon.beacon.*
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import java.util.*


/**
 * Created by Harsh Rajgor on 22/04/20.
 */

class MyApplication : Application(), BootstrapNotifier, RangeNotifier {

    private val TAG = MainActivity::class.java.simpleName
    private val deviceID = UUID.randomUUID().toString()
    private val safeDistanceInMetres = 3.0
    private var regionBootstrap: RegionBootstrap? = null
    private lateinit var backgroundPowerSaver: BackgroundPowerSaver
    private lateinit var beaconManager: BeaconManager
    private var haveDetectedBeaconsSinceBoot = false
    private var notificationID = 456
    private lateinit var region: Region
    private var mainActivity: MainActivity? = null
    private lateinit var context: Context
    private var isNotificationSent = false
    private var sendNotificationAfterInMs = 15000L

    override fun onCreate() {
        super.onCreate()
        context = this
        isNotificationSent = false
        enableForegroundScanningService()
        beaconTransmissionInBackground()
        resetTimer()
    }

    private fun resetTimer() {
        Handler().postDelayed({
            isNotificationSent = false
            resetTimer()
        }, sendNotificationAfterInMs)
    }

    private fun enableForegroundScanningService() {
        beaconManager = BeaconManager.getInstanceForApplication(this)
        val title = "Scanning..."
        val body = "Scanning for persons nearby."
        val builder = NotificationManager().getNotificationBuilder(this, title, body)
        beaconManager.enableForegroundServiceScanning(builder.build(), notificationID)

        beaconManager.setEnableScheduledScanJobs(false)
        beaconManager.backgroundBetweenScanPeriod = 0
        beaconManager.backgroundScanPeriod = 1100

        region = Region(deviceID, null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
        backgroundPowerSaver = BackgroundPowerSaver(this)
    }

    private fun beaconTransmissionInBackground() {
        val beacon = Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118) // Radius Networks.  Change this for other beacon layouts
            .setTxPower(-59)
            .setDataFields(listOf(0L)) // Remove this for beacon layouts without d: fields
            .build()
        // Change the layout below for other beacon types
        // Change the layout below for other beacon types
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Log.e(TAG, "Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Log.i(TAG, "Advertisement start succeeded.")
            }
        })
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
        Log.i(TAG, "Current region state is: " + if (state == 1) "INSIDE" else "OUTSIDE ($state)")
    }

    override fun didEnterRegion(region: Region?) {
        Log.i(TAG, "Did enter region.")
        isNotificationSent = false
        try {
            beaconManager.addRangeNotifier(this)
            if (region != null) {
                beaconManager.startRangingBeaconsInRegion(region)
            }
        } catch (e: Exception) {
            Log.i(TAG, "Exception While Ranging " + e.message)
        }
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "auto launching MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)
            haveDetectedBeaconsSinceBoot = true
        } else {
            if (mainActivity == null) {
                haveDetectedBeaconsSinceBoot = false
            }
        }

    }

    override fun didExitRegion(region: Region?) {
        Log.i(TAG, "I no longer see a beacon")
        isNotificationSent = false
    }

    fun disableMonitoring() {
        if (regionBootstrap != null) {
            regionBootstrap!!.disable()
            regionBootstrap = null
        }
    }

    fun enableMonitoring() {
        region = Region(deviceID, null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
    }

    fun setMainActivity(activity: MainActivity?) {
        this.mainActivity = activity
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, p1: Region?) {
        if (beacons != null) {
            if (beacons.isNotEmpty()) {
                for (beacon in beacons) {
                    val distance = beacon.distance
                    if (distance < safeDistanceInMetres) {
                        val title = "Warning"
                        val message =
                            "I see a person that is ${String.format(
                                "%.2f",
                                beacon.distance
                            )} metres away. Please maintain $safeDistanceInMetres meters distance."
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
