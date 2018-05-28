package com.bachhuberdesign.xplaneopenmap.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.bachhuberdesign.xplaneopenmap.data.FlightMapViewModel
import com.bachhuberdesign.xplaneopenmap.data.UDPCallback
import com.bachhuberdesign.xplaneopenmap.data.UDPListener

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.milkcan.effortlessandroid.toastLong
import xplaneopenmap.bachhuberdesign.com.openmap.R
import java.net.DatagramPacket

class FlightMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var viewModel: FlightMapViewModel

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

        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun showIpAddressDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }

        val ipAddress = AndroidHelper.getLocalIpAddress()

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
            // TODO: Handle display of flight location data
        })
    }

}
