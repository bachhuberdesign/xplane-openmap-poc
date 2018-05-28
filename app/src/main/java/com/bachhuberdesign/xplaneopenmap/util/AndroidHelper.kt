import io.milkcan.effortlessandroid.e
import java.net.Inet4Address
import java.net.NetworkInterface

object AndroidHelper {

    @JvmStatic
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()

            while (interfaces.hasMoreElements()) {
                val inetAddresses = interfaces.nextElement().inetAddresses

                while (inetAddresses.hasMoreElements()) {
                    val address = inetAddresses.nextElement()

                    if (!address.isLoopbackAddress && address is Inet4Address) {
                        return address.getHostAddress()
                    }
                }
            }
        } catch (ex: Exception) {
            e("Exception obtaining local IP address: ${ex.message}", ex)
        }

        return null
    }

}