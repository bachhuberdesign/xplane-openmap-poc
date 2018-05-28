package com.bachhuberdesign.xplaneopenmap.data

import java.net.DatagramPacket

interface UDPCallback {

    fun onNextPacket(packet: DatagramPacket)

}