package com.bachhuberdesign.xplaneopenmap.data.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.bachhuberdesign.xplaneopenmap.data.UDPCallback
import com.bachhuberdesign.xplaneopenmap.data.UDPListener
import com.bachhuberdesign.xplaneopenmap.data.model.FlightPathWrapper
import io.milkcan.effortlessandroid.d
import java.net.DatagramPacket
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.ByteBuffer

class FlightMapViewModel : ViewModel(), UDPCallback {

    private val flightPathLiveData: MutableLiveData<FlightPathWrapper> = MutableLiveData()
    private val messageLiveData: MutableLiveData<String> = MutableLiveData()
    private var udpListener: UDPListener? = null

    private var lastPitch: Float = 0.0f
    private var lastRoll: Float = 0.0f
    private var lastHeading: Float = 0.0f
    private var lastLatitude: Float = 0.0f
    private var lastLongitude: Float = 0.0f
    private var lastAltitude: Float = 0.0f
    private var lastSpeed: Float = 0.0f

    override fun onCleared() {
        udpListener?.kill()

        super.onCleared()
    }

    override fun onNextPacket(packet: DatagramPacket) {
        val data = packet.data

        val dataSetIndex: Int = data[5].toInt()
        val floatArray = getFloatsFromDataPacket(data)

        when (dataSetIndex) {
            17 -> handlePitchRollHeadings(floatArray)
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
        lastLatitude = floats[0]
        lastLongitude = floats[1]
        lastAltitude = floats[2]

        refreshFlightData()
    }

    private fun handlePitchRollHeadings(floats: FloatArray) {
        lastPitch = floats[0]
        lastRoll = floats[1]
        lastHeading = floats[3]

        refreshFlightData()
    }

    private fun handleLocationVelocityDistanceTravelled(floats: FloatArray) {
        // TODO:
        refreshFlightData()
    }

    private fun handleAllPlanesLatitude(floats: FloatArray) {
        // TODO:
    }

    private fun handleAllPlanesLongitude(floats: FloatArray) {
        // TODO:
    }

    private fun refreshFlightData() {
        val data = FlightPathWrapper(
                latitude = lastLatitude,
                longitude = lastLongitude,
                pitch = lastPitch,
                heading = lastHeading,
                roll = lastRoll,
                speed = lastSpeed,
                altitude = lastAltitude
        )

        if (flightPathLiveData.value != data) {
            flightPathLiveData.postValue(data)
        } else {
            d("Flight data not changed, ignoring: $data")
        }
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
                .map {
                    var float = ByteBuffer.wrap(it).order(LITTLE_ENDIAN).float
                    if (float == -999.0f) {
                        float = 0.0f
                    }

                    float
                }
                .toFloatArray()
    }

}