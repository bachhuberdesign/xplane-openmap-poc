package com.bachhuberdesign.xplaneopenmap.data

import io.milkcan.effortlessandroid.e
import java.net.DatagramPacket
import java.net.DatagramSocket

open class UDPListener(private val callback: UDPCallback) : Thread() {

    companion object {
        const val MAX_UDP_DATAGRAM_LENGTH = 41
    }

    private var isKeepRunning = true

    override fun run() {
        val packet = DatagramPacket(ByteArray(MAX_UDP_DATAGRAM_LENGTH), MAX_UDP_DATAGRAM_LENGTH)

        var socket: DatagramSocket? = null

        try {
            socket = DatagramSocket(48001)

            while (isKeepRunning) {
                socket.receive(packet)
                callback.onNextPacket(packet)
            }
        } catch (ex: Exception) {
            e("Error handling datagram: ${ex.message}", ex)
        } finally {
            socket?.close()
        }
    }

    fun kill() {
        isKeepRunning = false
    }

}
