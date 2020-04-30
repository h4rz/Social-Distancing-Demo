package com.h4rz.socialdistancing.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.h4rz.socialdistancing.R
import com.h4rz.socialdistancing.application.MyApplication
import com.h4rz.socialdistancing.receiver.MyBroadcastReceiver
import com.h4rz.socialdistancing.service.LocationUpdatesService
import com.h4rz.socialdistancing.utility.Constants.ACTION_BROADCAST
import com.h4rz.socialdistancing.utility.Constants.BLUETOOTH_INTENT_REQUEST_CODE
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var context: Context
    private var isXiaomiDevice = false
    private lateinit var myReceiver: MyBroadcastReceiver
    private var mService: LocationUpdatesService? = null
    private var mBound = false

    // Monitors the state of the connection to the service.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: LocationUpdatesService.LocalBinder =
                service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
            enableLocation()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(
            Intent(this, LocationUpdatesService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeVariables()
        askForPermissions()
    }

    private fun checkIfXiaomiDevice() {
        if (Build.MANUFACTURER.toLowerCase() == "xiaomi")
            btnDisableBatterySaver.visibility = View.VISIBLE
        else
            btnDisableBatterySaver.visibility = View.GONE
    }

    private fun disableBatterySaverForMiDevices() {
        if (Build.MANUFACTURER.toLowerCase() == "xiaomi") {
            try {
                val intent = Intent()
                intent.component = ComponentName(
                    "com.miui.powerkeeper",
                    "com.miui.powerkeeper.ui.HiddenAppsConfigActivity"
                )
                intent.putExtra("package_name", packageName)
                intent.putExtra("package_label", getText(R.string.app_name))
                startActivity(intent)
            } catch (anfe: ActivityNotFoundException) {
                anfe.printStackTrace()
            }
        }
    }

    private fun initializeVariables() {
        context = this
        btnDisableBatterySaver.setOnClickListener(this)

        // Show Battery saver button only when manufacturer is XIAOMI
        checkIfXiaomiDevice()

        // for location tracking
        myReceiver = MyBroadcastReceiver()
    }

    private fun askForPermissions() = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            runWithPermissions(Manifest.permission.FOREGROUND_SERVICE) {
                enableBluetoothAndLocation()
            }
        } else {
            enableBluetoothAndLocation()
        }
    }

    private fun enableBluetoothAndLocation() {
        enableBluetooth()
        enableLocation()
    }

    private fun enableLocation() {
        if (!canGetLocation())
            showSettingsAlert()
        // Permission was granted.
        mService?.requestLocationUpdates()
    }

    private fun enableBluetooth() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, BLUETOOTH_INTENT_REQUEST_CODE)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BLUETOOTH_INTENT_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK)
                    enableBluetooth()
            }
        }
    }*/

    private fun canGetLocation(): Boolean {
        var gpsEnabled = false
        var networkEnabled = false
        val lm: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            networkEnabled = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        return !(!gpsEnabled || !networkEnabled)
    }

    private fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setCancelable(false)
        // Setting Dialog Title
        alertDialog.setTitle("Location Access Needed")
        // Setting Dialog Message
        alertDialog.setMessage("Please enable location services to identify distance between the nearby person.")

        // On pressing Settings button
        alertDialog.setPositiveButton("OK") { _, _ ->
            val intent = Intent(
                ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(intent)
        }
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        (applicationContext as MyApplication).setMainActivity(null)
        (applicationContext as MyApplication).enableMonitoring()
    }

    override fun onPause() {
        (applicationContext as MyApplication).setMainActivity(null)
        (applicationContext as MyApplication).enableMonitoring()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        (applicationContext as MyApplication).setMainActivity(this)
        (applicationContext as MyApplication).enableMonitoring()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(myReceiver, IntentFilter(ACTION_BROADCAST))
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection)
            mBound = false
        }
        super.onStop()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDisableBatterySaver -> {
                disableBatterySaverForMiDevices()
            }
        }
    }

}
