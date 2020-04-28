package com.h4rz.socialdistancing.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.h4rz.socialdistancing.R
import com.h4rz.socialdistancing.application.MyApplication
import com.h4rz.socialdistancing.utility.Constants.BLUETOOTH_INTENT_REQUEST_CODE
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var context: Context
    private var isXiaomiDevice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeVariables()
        askForPermissions()
    }

    private fun checkIfXiaomiDevice() {
        if(Build.MANUFACTURER.toLowerCase() == "xiaomi")
            btnDisableBatterySaver.visibility = View.VISIBLE
        else
            btnDisableBatterySaver.visibility = View.GONE
    }

    private fun disableBatterySaverForMiDevices() {
        if(Build.MANUFACTURER.toLowerCase() == "xiaomi") {
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
        super.onPause()
        (applicationContext as MyApplication).setMainActivity(null)
        (applicationContext as MyApplication).enableMonitoring()
    }

    override fun onResume() {
        super.onResume()
        (applicationContext as MyApplication).setMainActivity(this)
        (applicationContext as MyApplication).enableMonitoring()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnDisableBatterySaver -> {
                disableBatterySaverForMiDevices()
            }
        }
    }

}
