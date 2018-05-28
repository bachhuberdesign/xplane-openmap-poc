package com.bachhuberdesign.xplaneopenmap.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.location.Location
import java.net.DatagramPacket

class FlightMapViewModel : ViewModel(), UDPCallback {

    private val flightPathLiveData: MutableLiveData<Location> = MutableLiveData()
    private val messageLiveData: MutableLiveData<String> = MutableLiveData()
    private var udpListener: UDPListener? = null

    override fun onCleared() {
        udpListener?.kill()

        super.onCleared()
    }

    override fun onNextPacket(packet: DatagramPacket) {
        val data = packet.data

        // TODO: Do something with received packet
    }

    fun startUDPClient() {
        udpListener = UDPListener(this)
    }

    fun getFlightPathStream(): LiveData<Location> = flightPathLiveData

    fun getMessageStream(): LiveData<String> = messageLiveData

}