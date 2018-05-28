package com.bachhuberdesign.xplaneopenmap.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.bachhuberdesign.xplaneopenmap.data.viewmodel.FlightMapViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.milkcan.effortlessandroid.d
import io.milkcan.effortlessandroid.toastLong
import xplaneopenmap.bachhuberdesign.com.openmap.R

class FlightMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var viewModel: FlightMapViewModel

    private var cameraMoved: Boolean = false
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        initViewModel()
        showIpAddressDialog()
    }

    override fun onDestroy() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }

        super.onDestroy()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    private fun showIpAddressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }

        val ipAddress = AndroidHelper.getDeviceLocalIpAddress()

        val message = if (!ipAddress.isNullOrBlank()) {
            "Please set your X-Plane data export to your device's IP address: $ipAddress"
        } else {
            "Error getting IP address for the local device. Please check and make sure that you are connected to the Wi"
        }

        dialog = AlertDialog.Builder(this)
                .setTitle("Data Export Config")
                .setMessage(message)
                .setPositiveButton("Done") { _, _ -> run {} }
                .show()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(FlightMapViewModel::class.java)

        viewModel.startUDPClient()

        viewModel.getMessageStream().observe(this, Observer { toastLong("$it") })

        viewModel.getFlightPathStream().observe(this, Observer {
            val position = LatLng(it!!.latitude.toDouble(), it.longitude.toDouble())
            d("Flight path position: $position")
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 12.0f)

            map.clear()

            if (!cameraMoved){
                map.moveCamera(cameraUpdate)
                cameraMoved = true
            }

            map.addMarker(MarkerOptions().position(position).title("Current Flight"))
        })
    }

}
