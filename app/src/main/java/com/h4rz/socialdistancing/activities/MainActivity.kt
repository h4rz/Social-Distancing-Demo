package com.h4rz.socialdistancing.activities

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.h4rz.socialdistancing.R
import com.h4rz.socialdistancing.application.MyApplication
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var context: Context
    private var BLUETOOTH_INTENT_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeVariables()
        askForPermissions()
    }

    private fun initializeVariables() {
        context = this
    }

    private fun askForPermissions() = runWithPermissions(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            runWithPermissions(Manifest.permission.FOREGROUND_SERVICE) {
                enableBluetooth()
            }
        } else {
            enableBluetooth()
        }
    }

    private fun enableBluetooth() {
        val eintent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(eintent, BLUETOOTH_INTENT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BLUETOOTH_INTENT_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK)
                    enableBluetooth()
            }
        }
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

}
