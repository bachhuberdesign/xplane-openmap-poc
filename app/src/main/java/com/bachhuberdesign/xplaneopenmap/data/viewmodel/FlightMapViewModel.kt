package com.bachhuberdesign.xplaneopenmap.data.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.bachhuberdesign.xplaneopenmap.data.UDPCallback
import com.bachhuberdesign.xplaneopenmap.data.UDPListener
import com.bachhuberdesign.xplaneopenmap.data.model.FlightPathWrapper
import io.milkcan.effortlessandroid.d
import java.net.DatagramPacket
import java.util.*
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteBuffer

class FlightMapViewModel : ViewModel(), UDPCallback {

    private val flightPathLiveData: MutableLiveData<FlightPathWrapper> = MutableLiveData()
    private val messageLiveData: MutableLiveData<String> = MutableLiveData()
    private var udpListener: UDPListener? = null

    override fun onCleared() {
        udpListener?.kill()

        super.onCleared()
    }

    override fun onNextPacket(packet: DatagramPacket) {
        val data = packet.data

        d("Packet received: ${Arrays.toString(data)}")

        val dataSetIndex: Int = data[5].toInt()
        val floatArray = getFloatsFromDataPacket(data)

        when (dataSetIndex) {
            20 -> handleLatitudeLongitudeAltitude(floatArray)
            21 -> handleLocationVelocityDistanceTravelled(floatArray)
            22 -> handleAllPlanesLatitude(floatArray)
            23 -> handleAllPlanesLongitude(floatArray)
        }
    }

    fun startUDPClient() {
        udpListener = UDPListener(this)
        udpListener!!.start()
    }

    fun getFlightPathStream(): LiveData<FlightPathWrapper> = flightPathLiveData

    fun getMessageStream(): LiveData<String> = messageLiveData

    private fun handleLatitudeLongitudeAltitude(floats: FloatArray) {
        floats.forEachIndexed { i, float -> d("handleLatitudeLongitudeAltitude $i: $float") }

        val latitude = floats[0]
        val longitude = floats[1]

        flightPathLiveData.postValue(FlightPathWrapper(latitude, longitude))
    }

    private fun handleLocationVelocityDistanceTravelled(floats: FloatArray) {

    }

    private fun handleAllPlanesLatitude(floats: FloatArray) {

    }

    private fun handleAllPlanesLongitude(floats: FloatArray) {

    }

    private fun getFloatsFromDataPacket(data: ByteArray): FloatArray {
        return arrayOf(
                byteArrayOf(data[9], data[10], data[11], data[12]),
                byteArrayOf(data[13], data[14], data[15], data[16]),
                byteArrayOf(data[17], data[18], data[19], data[20]),
                byteArrayOf(data[21], data[22], data[23], data[24]),
                byteArrayOf(data[25], data[26], data[27], data[28]),
                byteArrayOf(data[29], data[30], data[31], data[32]),
                byteArrayOf(data[33], data[34], data[35], data[36]),
                byteArrayOf(data[37], data[38], data[39], data[40])
        )
                .map { ByteBuffer.wrap(it).order(LITTLE_ENDIAN).float }
                .toFloatArray()
    }

}